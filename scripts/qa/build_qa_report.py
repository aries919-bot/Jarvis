#!/usr/bin/env python3
"""Build a self-contained Aries full-app QA report from Android test artefacts."""

from __future__ import annotations

import argparse
import datetime as dt
import html
import json
import os
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Any


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return ""


def parse_junit(root: Path) -> dict[str, Any]:
    files = list(root.glob("app/build/outputs/androidTest-results/**/*.xml"))
    files += list(root.glob("app/build/test-results/**/*.xml"))
    files += list(root.glob("app/build/outputs/test-results/**/*.xml"))
    seen: set[Path] = set()
    files = [p for p in files if not (p in seen or seen.add(p))]
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    cases: list[dict[str, str]] = []
    for file in files:
        try:
            tree = ET.parse(file)
        except (ET.ParseError, OSError):
            continue
        # Count leaf test suites only. A <testsuites> root repeats the aggregate totals
        # of its child <testsuite> nodes, so counting both would double the result.
        for suite in tree.iter("testsuite"):
            for key in totals:
                try:
                    totals[key] += int(suite.attrib.get(key, "0"))
                except ValueError:
                    pass
        for case in tree.iter("testcase"):
            outcome = "passed"
            message = ""
            for child in list(case):
                if child.tag in {"failure", "error", "skipped"}:
                    outcome = child.tag
                    message = (child.attrib.get("message") or (child.text or "")).strip()[:1200]
                    break
            cases.append({
                "name": case.attrib.get("name", "unknown"),
                "classname": case.attrib.get("classname", ""),
                "time": case.attrib.get("time", ""),
                "outcome": outcome,
                "message": message,
                "file": str(file.relative_to(root)) if file.is_relative_to(root) else str(file),
            })
    return {"totals": totals, "cases": cases, "files": [str(p) for p in files]}


def parse_ui_bundle(qa_dir: Path) -> dict[str, Any]:
    candidates = list(qa_dir.rglob("ui-audit.json"))
    if not candidates:
        return {"screens": [], "findings": [], "device": {}, "bundle_found": False}
    try:
        data = json.loads(candidates[0].read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return {"screens": [], "findings": [], "device": {}, "bundle_found": False}
    data["bundle_found"] = True
    data["bundle_dir"] = str(candidates[0].parent)
    return data


def ui_screen_dimension(qa_dir: Path, key: str, default: int = 0) -> int:
    bundles = list(qa_dir.rglob("ui-audit.json"))
    if not bundles:
        return default
    try:
        data = json.loads(bundles[0].read_text(encoding="utf-8"))
        return int(data.get("device", {}).get(key, default))
    except (OSError, ValueError, json.JSONDecodeError):
        return default


def inspect_hierarchies(qa_dir: Path, density: float) -> list[dict[str, Any]]:
    findings: list[dict[str, Any]] = []
    screen_width = ui_screen_dimension(qa_dir, "screenWidthPx", 0)
    screen_height = ui_screen_dimension(qa_dir, "screenHeightPx", 0)
    for file in qa_dir.rglob("*.xml"):
        if file.name.startswith("TEST-"):
            continue
        try:
            root = ET.parse(file).getroot()
        except (ET.ParseError, OSError):
            continue
        for node in root.iter("node"):
            if node.attrib.get("clickable") != "true":
                continue
            bounds = node.attrib.get("bounds", "")
            match = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", bounds)
            if not match:
                continue
            x1, y1, x2, y2 = map(int, match.groups())
            width_dp = (x2 - x1) / max(density, 0.1)
            height_dp = (y2 - y1) / max(density, 0.1)
            text_value = (node.attrib.get("text") or "").strip()
            desc = (node.attrib.get("content-desc") or "").strip()
            screen = file.stem
            label = text_value or desc
            if width_dp < 48 or height_dp < 48:
                findings.append({
                    "severity": "warning",
                    "category": "touch-target",
                    "screen": screen,
                    "message": f"Clickable target below 48dp ({width_dp:.1f} x {height_dp:.1f}dp).",
                    "label": label,
                })
            if not label:
                findings.append({
                    "severity": "warning",
                    "category": "accessibility-label",
                    "screen": screen,
                    "message": "Clickable element has no text or content description.",
                    "label": "",
                })
            if x1 < 0 or y1 < 0 or (screen_width and x2 > screen_width) or (screen_height and y2 > screen_height):
                findings.append({
                    "severity": "warning",
                    "category": "screen-clipping",
                    "screen": screen,
                    "message": "Clickable element begins outside the visible screen bounds.",
                    "label": label,
                })
    return findings


def ui_density_from_bundle(qa_dir: Path) -> float:
    bundles = list(qa_dir.rglob("ui-audit.json"))
    if not bundles:
        return 1.0
    try:
        data = json.loads(bundles[0].read_text(encoding="utf-8"))
        return float(data.get("device", {}).get("density", 1.0) or 1.0)
    except (OSError, ValueError, json.JSONDecodeError):
        return 1.0


def measure_screen_density(qa_dir: Path) -> tuple[list[dict[str, Any]], list[dict[str, Any]]]:
    metrics: list[dict[str, Any]] = []
    findings: list[dict[str, Any]] = []
    density = max(float(ui_density_from_bundle(qa_dir)), 0.1)
    for file in qa_dir.rglob("*.xml"):
        if file.name.startswith("TEST-"):
            continue
        try:
            root = ET.parse(file).getroot()
        except (ET.ParseError, OSError):
            continue
        texts = []
        clickable_count = 0
        small_targets = 0
        unlabeled_targets = 0
        for node in root.iter("node"):
            value = (node.attrib.get("text") or "").strip()
            if value:
                texts.append(value)
            if node.attrib.get("clickable") == "true":
                clickable_count += 1
                bounds_match = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", node.attrib.get("bounds", ""))
                if bounds_match:
                    x1, y1, x2, y2 = map(int, bounds_match.groups())
                    if (x2 - x1) / density < 48 or (y2 - y1) / density < 48:
                        small_targets += 1
                if not value and not (node.attrib.get("content-desc") or "").strip():
                    unlabeled_targets += 1
        if not texts:
            continue
        total_chars = sum(len(value) for value in texts)
        long_blocks = [value for value in texts if len(value) >= 120]
        stat = {
            "screen": file.stem,
            "visibleTextNodes": len(texts),
            "totalTextCharacters": total_chars,
            "longTextBlocks": len(long_blocks),
            "longTextSamples": [value[:180] for value in long_blocks[:4]],
            "clickableCount": clickable_count,
            "smallTouchTargets": small_targets,
            "unlabeledTargets": unlabeled_targets,
        }
        metrics.append(stat)
        if len(texts) > 36 or len(long_blocks) > 5:
            findings.append({
                "severity": "review",
                "category": "content-density",
                "screen": file.stem,
                "message": (
                    f"Potentially dense screen: {len(texts)} visible text nodes, "
                    f"{total_chars} characters and {len(long_blocks)} long text blocks. "
                    "Review screenshot for hierarchy, grouping and unnecessary repetition."
                ),
            })
    return metrics, findings


def static_source_audit(root: Path) -> dict[str, Any]:
    main_path = root / "app/src/main/java/com/cem/jarvis/MainActivity.kt"
    career_path = root / "app/src/main/java/com/cem/jarvis/CareerActivity.kt"
    manifest_path = root / "app/src/main/AndroidManifest.xml"
    gradle_path = root / "app/build.gradle.kts"
    main = read_text(main_path)
    career = read_text(career_path)
    manifest = read_text(manifest_path)
    gradle = read_text(gradle_path)

    page_builders = re.findall(r"pages\.add\(\s*build([A-Za-z0-9_]+)\(\)\s*\)", main)
    numeric_routes = sorted({int(x) for x in re.findall(r"showPage\(\s*(\d+)\s*\)", main)})
    unreachable_pages = [
        {"index": index, "builder": builder}
        for index, builder in enumerate(page_builders)
        if index not in numeric_routes
    ]

    small_text = []
    for file_name, source in (("MainActivity.kt", main), ("CareerActivity.kt", career)):
        for line_no, line in enumerate(source.splitlines(), start=1):
            for match in re.finditer(r"(?:textSize\s*=\s*|label\([^,\n]+,\s*)(\d+(?:\.\d+)?)f", line):
                try:
                    size = float(match.group(1))
                except ValueError:
                    continue
                if size < 11:
                    small_text.append({
                        "file": file_name,
                        "line": line_no,
                        "sp": size,
                        "snippet": line.strip()[:220],
                    })

    activities = re.findall(r'<activity\b[^>]*android:name="([^"]+)"[^>]*>', manifest, flags=re.S)
    package = re.search(r'applicationId\s*=\s*"([^"]+)"', gradle)
    version_name = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
    version_code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
    warnings: list[dict[str, Any]] = []
    for item in unreachable_pages:
        warnings.append({
            "severity": "warning",
            "category": "navigation-reachability",
            "screen": "main-navigation",
            "message": f"MainActivity page index {item['index']} ({item['builder']}) has no direct showPage(index) route found by source scan. Confirm whether this screen is intentionally hidden.",
        })
    if small_text:
        warnings.append({
            "severity": "review",
            "category": "readability",
            "screen": "source-review",
            "message": f"{len(small_text)} text declarations below 11sp were found. Inspect screenshots on a small phone before changing them; compact chips/metadata may be intentional.",
        })

    return {
        "package": package.group(1) if package else "",
        "versionName": version_name.group(1) if version_name else "",
        "versionCode": int(version_code.group(1)) if version_code else None,
        "mainPages": [{"index": i, "builder": b} for i, b in enumerate(page_builders)],
        "numericRoutes": numeric_routes,
        "unreachablePages": unreachable_pages,
        "activities": activities,
        "smallTextDeclarations": small_text,
        "warnings": warnings,
    }


def parse_runtime_metrics(qa_dir: Path, ui: dict[str, Any]) -> dict[str, Any]:
    metrics = dict(ui.get("metrics", {}))
    meminfo_files = list(qa_dir.rglob("meminfo.txt"))
    gfxinfo_files = list(qa_dir.rglob("gfxinfo.txt"))
    if meminfo_files:
        meminfo = read_text(meminfo_files[0])
        total_pss = re.search(r"(?im)^\s*TOTAL\s+PSS:\s*([\d,]+)\s*kB", meminfo)
        java_heap = re.search(r"(?im)^\s*Java Heap:\s*([\d,]+)\s*kB", meminfo)
        native_heap = re.search(r"(?im)^\s*Native Heap:\s*([\d,]+)\s*kB", meminfo)
        if total_pss:
            metrics["totalPssKb"] = int(total_pss.group(1).replace(",", ""))
        if java_heap:
            metrics["javaHeapKb"] = int(java_heap.group(1).replace(",", ""))
        if native_heap:
            metrics["nativeHeapKb"] = int(native_heap.group(1).replace(",", ""))
    if gfxinfo_files:
        gfxinfo = read_text(gfxinfo_files[0])
        total_frames = re.search(r"(?im)^\s*Total frames rendered:\s*(\d+)", gfxinfo)
        janky_frames = re.search(r"(?im)^\s*Janky frames:\s*(\d+)", gfxinfo)
        if total_frames:
            metrics["totalFramesRendered"] = int(total_frames.group(1))
        if janky_frames:
            metrics["jankyFrames"] = int(janky_frames.group(1))
    return metrics


def scan_logcat(qa_dir: Path) -> list[dict[str, str]]:
    logs = list(qa_dir.rglob("logcat.txt"))
    if not logs:
        return []
    text = read_text(logs[0])
    patterns = [
        ("crash", re.compile(r"FATAL EXCEPTION|Fatal signal \d+", re.I)),
        ("anr", re.compile(r"\bANR in\b|Application Not Responding", re.I)),
        ("process-death", re.compile(r"Process .* has died|Killing \d+:com\.cem\.jarvis", re.I)),
    ]
    results = []
    lines = text.splitlines()
    for category, pattern in patterns:
        hits = [i for i, line in enumerate(lines) if pattern.search(line)]
        for i in hits[:30]:
            results.append({
                "severity": "error" if category in {"crash", "anr"} else "warning",
                "category": category,
                "message": "\n".join(lines[max(0, i - 1):min(len(lines), i + 4)])[:1600],
            })
    return results


def parse_pipeline_status(qa_dir: Path) -> dict[str, Any]:
    status: dict[str, Any] = {}
    for name in ("build-status.json", "emulator-status.json"):
        matches = list(qa_dir.rglob(name))
        if not matches:
            continue
        try:
            status[name.removesuffix(".json")] = json.loads(matches[0].read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError):
            status[name.removesuffix(".json")] = {"parseError": True}
    return status


def lint_summary(root: Path) -> dict[str, Any]:
    files = list(root.glob("app/build/reports/lint-results*.xml"))
    result = {"found": False, "errors": 0, "warnings": 0, "issues": []}
    if not files:
        return result
    result["found"] = True
    try:
        tree = ET.parse(files[0])
        for issue in tree.iter("issue"):
            severity = issue.attrib.get("severity", "").lower()
            if severity == "error":
                result["errors"] += 1
            elif severity == "warning":
                result["warnings"] += 1
            result["issues"].append({
                "id": issue.attrib.get("id", ""),
                "severity": severity,
                "message": issue.attrib.get("message", ""),
                "category": issue.attrib.get("category", ""),
                "file": next((loc.attrib.get("file", "") for loc in issue.findall("location")), ""),
            })
    except (ET.ParseError, OSError):
        pass
    return result


def render_html(report: dict[str, Any], out_dir: Path) -> None:
    summary = report["summary"]
    issues = report["findings"]
    screens = report["ui"]["screens"]
    cases = report["tests"]["cases"]
    screenshots = []
    bundle_dir = Path(report["ui"].get("bundle_dir", "")) if report["ui"].get("bundle_dir") else None
    if bundle_dir and bundle_dir.exists():
        for screen in screens:
            name = screen.get("screenshot")
            if name and (bundle_dir / name).exists():
                rel = os.path.relpath(bundle_dir / name, out_dir)
                screenshots.append((screen.get("name", ""), rel.replace("\\", "/")))

    def esc(value: Any) -> str:
        return html.escape(str(value if value is not None else ""))

    issue_rows = "".join(
        f"<tr><td>{esc(x.get('severity'))}</td><td>{esc(x.get('category'))}</td><td>{esc(x.get('screen'))}</td><td>{esc(x.get('message'))}</td></tr>"
        for x in issues
    ) or '<tr><td colspan="4">No automated findings recorded.</td></tr>'
    stats_by_name = {s.get("screen"): s for s in report["ui"].get("screenStats", [])}
    screen_rows = "".join(
        f"<tr><td>{esc(s.get('name'))}</td><td>{esc(stats_by_name.get(s.get('name'), {}).get('clickableCount', s.get('clickableCount', 0)))}</td><td>{esc(stats_by_name.get(s.get('name'), {}).get('smallTouchTargets', s.get('smallTouchTargets', 0)))}</td><td>{esc(stats_by_name.get(s.get('name'), {}).get('unlabeledTargets', s.get('unlabeledTargets', 0)))}</td><td>{esc(stats_by_name.get(s.get('name'), {}).get('visibleTextNodes', 0))}</td><td>{esc(stats_by_name.get(s.get('name'), {}).get('longTextBlocks', 0))}</td></tr>"
        for s in screens
    ) or '<tr><td colspan="6">No UI screens captured.</td></tr>'
    test_rows = "".join(
        f"<tr><td>{esc(t.get('classname'))}</td><td>{esc(t.get('name'))}</td><td>{esc(t.get('outcome'))}</td><td>{esc(t.get('time'))}</td></tr>"
        for t in cases
    ) or '<tr><td colspan="4">No JUnit XML results found in supplied artefacts.</td></tr>'
    shot_cards = "".join(
        f'<figure><img src="{esc(src)}" loading="lazy" alt="{esc(name)}"><figcaption>{esc(name)}</figcaption></figure>'
        for name, src in screenshots
    )
    visual_note = (
        "Screenshots are supplied as visual evidence. Contrast, visual hierarchy, task clarity and perceived clutter still require human review; this report does not assign an automatic design score."
    )
    runtime = report.get("runtimeMetrics", {})

    doc = f"""<!doctype html>
<html lang="tr"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Aries - Tam Uygulama QA Raporu</title>
<style>
:root{{--bg:#10151e;--panel:#192230;--line:#2b3747;--fg:#edf3fa;--muted:#a8b7c8;--ok:#35c58a;--warn:#f5b84b;--bad:#ff7373;}}
*{{box-sizing:border-box}}body{{margin:0;background:var(--bg);color:var(--fg);font:15px/1.55 system-ui,sans-serif}}
main{{max-width:1100px;margin:auto;padding:24px}}h1,h2{{line-height:1.2}}h1{{font-size:28px}}h2{{font-size:20px;margin-top:30px}}
p,.muted{{color:var(--muted)}}.grid{{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px}}
.card{{background:var(--panel);border:1px solid var(--line);border-radius:14px;padding:16px}}
.big{{font-size:26px;font-weight:750}}table{{width:100%;border-collapse:collapse;background:var(--panel);border-radius:12px;overflow:hidden}}
td,th{{padding:10px;border-bottom:1px solid var(--line);text-align:left;vertical-align:top}}th{{color:var(--muted)}}
.bad{{color:var(--bad)}}.warn{{color:var(--warn)}}.ok{{color:var(--ok)}}.screenshots{{display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:14px}}
figure{{margin:0;background:var(--panel);border:1px solid var(--line);padding:10px;border-radius:12px}}
img{{width:100%;height:auto;border-radius:8px}}figcaption{{padding:8px;color:var(--muted)}}
code{{overflow-wrap:anywhere}}.note{{border-left:3px solid var(--warn);padding:10px 14px;background:var(--panel)}}
</style></head><body><main>
<h1>Aries · Tam Uygulama Kalite Raporu</h1>
<p>Oluşturulma: {esc(report["generatedAt"])} · Paket: {esc(report["source"]["package"])} · Sürüm: {esc(report["source"]["versionName"])} ({esc(report["source"]["versionCode"])})</p>
<div class="grid">
<div class="card"><div class="muted">Otomatik UI ekranı</div><div class="big">{len(screens)}</div></div>
<div class="card"><div class="muted">JUnit testleri</div><div class="big">{summary["tests"]}</div></div>
<div class="card"><div class="muted">Test başarısızlığı</div><div class="big {'bad' if summary['testFailures'] else 'ok'}">{summary["testFailures"]}</div></div>
<div class="card"><div class="muted">Otomatik bulgu</div><div class="big">{len(issues)}</div></div>
<div class="card"><div class="muted">Lint hata / uyarı</div><div class="big">{report["lint"]["errors"]} / {report["lint"]["warnings"]}</div></div>
</div>
<h2>Çalıştırma kapsamı</h2><p>{esc(report["coverage"])} </p>
<div class="note">{esc(visual_note)}</div>
<h2>UI ekran ve kullanılabilirlik taraması</h2>
<table><thead><tr><th>Ekran</th><th>Tıklanabilir</th><th>48dp altı</th><th>Etiketsiz</th><th>Metin öğesi</th><th>Uzun blok</th></tr></thead><tbody>{screen_rows}</tbody></table>
<h2>Bulgu ve uyarılar</h2>
<table><thead><tr><th>Önem</th><th>Kategori</th><th>Ekran</th><th>Açıklama</th></tr></thead><tbody>{issue_rows}</tbody></table>
<h2>JUnit sonuçları</h2>
<table><thead><tr><th>Sınıf</th><th>Test</th><th>Sonuç</th><th>Süre (s)</th></tr></thead><tbody>{test_rows}</tbody></table>
<h2>Ekran görüntüleri</h2><div class="screenshots">{shot_cards}</div>
<h2>Açılış ve performans göstergeleri</h2>
<table><tbody>
<tr><th>Ana ekranın görünür olması</th><td>{esc(runtime.get('launchToVisibleMs', 'n/a'))} ms</td></tr>
<tr><th>Toplam bellek (PSS)</th><td>{esc(runtime.get('totalPssKb', 'n/a'))} KB</td></tr>
<tr><th>Java / Native heap</th><td>{esc(runtime.get('javaHeapKb', 'n/a'))} / {esc(runtime.get('nativeHeapKb', 'n/a'))} KB</td></tr>
<tr><th>Toplam / takılan kareler</th><td>{esc(runtime.get('totalFramesRendered', 'n/a'))} / {esc(runtime.get('jankyFrames', 'n/a'))}</td></tr>
</tbody></table>
<h2>Görsel tasarım ve kullanım kolaylığı değerlendirmesi</h2>
<p>Rapor; küçük dokunma alanları, metinsiz etkileşimli öğeler, ekran dışına taşma, navigasyona erişim, metin boyutu beyanları, görünür metin yoğunluğu, çökmeler ve test sonuçları gibi ölçülebilir sinyalleri toplar. Renk kontrastı, görsel hiyerarşi, görev akışının anlaşılır oluşu ve algılanan karışıklık için ekran görüntülerinin insan tarafından incelenmesi gerekir.</p>
<h2>Dosyalar</h2><p>Bu HTML raporunun yanında <code>report.json</code>, <code>ui-audit.json</code>, ekran görüntüleri, XML erişilebilirlik ağaçları ve Logcat kaydı saklanır.</p>
<p class="muted">Not: Test kapsamı emülatör ve test verileriyle sınırlıdır; gerçek SIM, Bluetooth aksesuarı, OEM pil kısıtlamaları, canlı kullanıcı hesapları ve gerçek işveren/ilan geçerliliği cihaz/haricî doğrulama gerektirir.</p>
</main></body></html>"""
    (out_dir / "index.html").write_text(doc, encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-root", default=".")
    parser.add_argument("--qa-dir", default="qa-output")
    parser.add_argument("--out-dir", default="qa-output/report")
    parser.add_argument("--api-level", default="")
    args = parser.parse_args()

    root = Path(args.repo_root).resolve()
    qa_dir = Path(args.qa_dir).resolve()
    out_dir = Path(args.out_dir).resolve()
    out_dir.mkdir(parents=True, exist_ok=True)

    source = static_source_audit(root)
    tests = parse_junit(root)
    ui = parse_ui_bundle(qa_dir)
    device_density = float(ui.get("device", {}).get("density", 1.0) or 1.0)
    ui_hierarchy_findings = inspect_hierarchies(qa_dir, device_density)
    screen_density, density_findings = measure_screen_density(qa_dir)
    lint = lint_summary(root)
    pipeline = parse_pipeline_status(qa_dir)
    runtime_metrics = parse_runtime_metrics(qa_dir, ui)

    # XML hierarchy analysis is authoritative for dimensions and labels. Remove the
    # equivalent instrumentation-side heuristics to avoid duplicate warnings.
    findings = [
        item for item in ui.get("findings", [])
        if item.get("category") not in {"touch-target", "accessibility-label"}
    ]
    findings.extend(ui_hierarchy_findings)
    findings.extend(density_findings)
    findings.extend(source.get("warnings", []))
    findings.extend(scan_logcat(qa_dir))
    findings.extend(
        {
            "severity": "error" if case["outcome"] in {"failure", "error"} else "warning",
            "category": "junit-" + case["outcome"],
            "screen": case["classname"],
            "message": case["name"] + (": " + case["message"] if case["message"] else ""),
        }
        for case in tests["cases"] if case["outcome"] != "passed"
    )
    findings.extend(
        {
            "severity": "error" if issue["severity"] == "error" else "warning",
            "category": "lint",
            "screen": issue.get("file", ""),
            "message": issue.get("id", "") + ": " + issue.get("message", ""),
        }
        for issue in lint["issues"]
    )

    totals = tests["totals"]
    summary = {
        "tests": totals["tests"],
        "testFailures": totals["failures"] + totals["errors"],
        "testSkipped": totals["skipped"],
        "screensCaptured": len(ui.get("screens", [])),
        "errorFindings": sum(1 for x in findings if x.get("severity") == "error"),
        "warningFindings": sum(1 for x in findings if x.get("severity") == "warning"),
        "reviewFindings": sum(1 for x in findings if x.get("severity") == "review"),
    }
    report = {
        "generatedAt": dt.datetime.now(dt.timezone.utc).isoformat(),
        "apiLevel": args.api_level,
        "source": source,
        "summary": summary,
        "tests": tests,
        "ui": {**ui, "screenStats": screen_density},
        "lint": lint,
        "pipeline": pipeline,
        "runtimeMetrics": runtime_metrics,
        "findings": findings,
        "coverage": "MainActivity home, settings, assistant/tasks, file manager, Film & TV, Piyasa Merkezi and all six CareerActivity tabs where reachable. Voice/microphone, external Bluetooth devices, OEM battery policies and destructive flows are intentionally not exercised.",
    }

    (out_dir / "report.json").write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    (out_dir / "summary.md").write_text(
        "\n".join([
            "# Aries Full App QA",
            f"- Generated: {report['generatedAt']}",
            f"- Package/version: {source['package']} {source['versionName']} ({source['versionCode']})",
            f"- Screens captured: {summary['screensCaptured']}",
            f"- JUnit tests: {summary['tests']}; failures: {summary['testFailures']}; skipped: {summary['testSkipped']}",
            f"- Findings: {summary['errorFindings']} errors, {summary['warningFindings']} warnings, {summary['reviewFindings']} manual review items",
            f"- Lint: {lint['errors']} errors, {lint['warnings']} warnings",
            f"- Pipeline exit statuses: {json.dumps(pipeline, ensure_ascii=False)}",
            f"- Runtime metrics: {json.dumps(runtime_metrics, ensure_ascii=False)}",
            "- Screenshots and XML hierarchy dumps are stored next to the UI audit bundle.",
            "- Visual clarity, contrast and task simplicity still require human review.",
        ]),
        encoding="utf-8",
    )
    render_html(report, out_dir)
    print(json.dumps({"output": str(out_dir), "summary": summary}, ensure_ascii=False))
    return 1 if summary["errorFindings"] > 0 or summary["testFailures"] > 0 else 0


if __name__ == "__main__":
    sys.exit(main())
