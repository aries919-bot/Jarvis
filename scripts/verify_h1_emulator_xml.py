#!/usr/bin/env python3
"""Fail CI unless Android instrumentation XML proves every expected test passed."""
from __future__ import annotations

import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

REPORT_ROOT = Path("app/build/outputs/androidTest-results/connected")
EXPECTED_TESTS = int(os.environ.get("H1_EXPECTED_TESTS", "4"))


def main() -> int:
    reports = sorted(REPORT_ROOT.rglob("TEST-*.xml")) if REPORT_ROOT.exists() else []
    if not reports:
        print(f"ERROR: no instrumentation XML found under {REPORT_ROOT}", file=sys.stderr)
        return 2

    total = failures = errors = skipped = 0
    for report in reports:
        try:
            root = ET.parse(report).getroot()
        except (ET.ParseError, OSError) as exc:
            print(f"ERROR: cannot parse {report}: {exc}", file=sys.stderr)
            return 2
        cases = list(root.iter("testcase"))
        report_tests = len(cases)
        report_failures = sum(len(case.findall("failure")) for case in cases)
        report_errors = sum(len(case.findall("error")) for case in cases)
        report_skipped = sum(len(case.findall("skipped")) for case in cases)
        total += report_tests
        failures += report_failures
        errors += report_errors
        skipped += report_skipped
        print(
            f"{report}: tests={report_tests} failures={report_failures} "
            f"errors={report_errors} skipped={report_skipped}"
        )

    print(
        f"TOTAL: tests={total} failures={failures} errors={errors} skipped={skipped}; "
        f"expected_tests={EXPECTED_TESTS}"
    )
    if total != EXPECTED_TESTS or failures or errors or skipped:
        print("ERROR: H1 instrumentation report does not satisfy the 4/4 pass gate.", file=sys.stderr)
        return 1
    print("PASS: every expected H1 instrumentation test passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
