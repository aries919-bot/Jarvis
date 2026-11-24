import json
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import h2_last_good_seed as seed


def feed(source_id, first_seen, title="Old", status="fresh", items=True):
    return {
        "schemaVersion": 1,
        "sourceId": source_id,
        "collectedAt": "2026-11-24T20:04:05Z",
        "sourceStatus": status,
        "items": ([{"id": "item-1", "title": title, "firstSeenAt": first_seen}] if items else []),
    }


class H2LastGoodSeedTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.seed_file = self.root / "seed.json"
        self.previous = self.root / "previous"

    def tearDown(self):
        self.tmp.cleanup()

    def put_seed(self, sources):
        self.seed_file.write_text(json.dumps({"schemaVersion": 1, "sources": sources}), encoding="utf-8")

    def put_current(self, value):
        self.previous.mkdir(parents=True, exist_ok=True)
        (self.previous / f"{value['sourceId']}.json").write_text(json.dumps(value), encoding="utf-8")

    def read_current(self, source_id):
        return json.loads((self.previous / f"{source_id}.json").read_text(encoding="utf-8"))

    def test_missing_feed_is_seeded_for_source_outage(self):
        self.put_seed([feed("tarsus-feed", "2026-11-24T20:00:39Z")])
        result = seed.seed_last_good(self.seed_file, self.previous)
        current = self.read_current("tarsus-feed")
        self.assertEqual(1, result["copied"])
        self.assertEqual("fresh", current["sourceStatus"])
        self.assertEqual("2026-11-24T20:00:39Z", current["items"][0]["firstSeenAt"])

    def test_stale_empty_cache_is_replaced_by_historical_last_good(self):
        self.put_seed([feed("tarsus-feed", "2026-11-24T20:00:39Z")])
        self.put_current(feed("tarsus-feed", None, status="stale", items=False))
        seed.seed_last_good(self.seed_file, self.previous)
        self.assertEqual("Old", self.read_current("tarsus-feed")["items"][0]["title"])

    def test_earliest_first_seen_is_merged_without_overwriting_current_payload(self):
        self.put_seed([feed("anadolu-feed", "2026-11-24T19:54:44Z", title="Historical")])
        self.put_current(feed("anadolu-feed", "2026-11-24T21:00:47Z", title="Current live title"))
        result = seed.seed_last_good(self.seed_file, self.previous)
        current = self.read_current("anadolu-feed")
        self.assertEqual(1, result["first_seen_merged"])
        self.assertEqual("Current live title", current["items"][0]["title"])
        self.assertEqual("2026-11-24T19:54:44Z", current["items"][0]["firstSeenAt"])

    def test_existing_earlier_first_seen_is_never_moved_forward(self):
        self.put_seed([feed("anadolu-feed", "2026-11-24T21:00:47Z")])
        self.put_current(feed("anadolu-feed", "2026-11-24T19:54:44Z"))
        seed.seed_last_good(self.seed_file, self.previous)
        self.assertEqual("2026-11-24T19:54:44Z", self.read_current("anadolu-feed")["items"][0]["firstSeenAt"])

    def test_unknown_item_ids_keep_current_first_seen(self):
        historical = feed("anadolu-feed", "2026-11-24T19:54:44Z")
        current = feed("anadolu-feed", "2026-11-24T21:00:47Z")
        current["items"].append({"id": "new-item", "firstSeenAt": "2026-11-24T21:00:47Z"})
        self.put_seed([historical])
        self.put_current(current)
        seed.seed_last_good(self.seed_file, self.previous)
        records = self.read_current("anadolu-feed")["items"]
        self.assertEqual("2026-11-24T19:54:44Z", records[0]["firstSeenAt"])
        self.assertEqual("2026-11-24T21:00:47Z", records[1]["firstSeenAt"])

    def test_stale_or_empty_historical_source_is_not_seeded(self):
        self.put_seed([feed("botas-feed", None, status="stale", items=False)])
        result = seed.seed_last_good(self.seed_file, self.previous)
        self.assertEqual(1, result["skipped"])
        self.assertFalse((self.previous / "botas-feed.json").exists())

    def test_invalid_seed_manifest_fails_closed(self):
        self.seed_file.write_text('{"schemaVersion":2,"sources":[]}', encoding="utf-8")
        with self.assertRaises(ValueError):
            seed.seed_last_good(self.seed_file, self.previous)


    def test_committed_seed_contains_five_historical_verified_feeds(self):
        path = ROOT / "career" / "h2_last_good_seed.json"
        manifest = json.loads(path.read_text(encoding="utf-8"))
        self.assertEqual(1, manifest["schemaVersion"])
        self.assertEqual(45476818354, manifest["provenance"]["runId"])
        sources = manifest["sources"]
        ids = {source["sourceId"] for source in sources}
        self.assertEqual({
            "anadolu-sozlesmeli-2026", "atk-personel-2026-2",
            "duzce-ogretim-elemani-2026", "tarsus-ogretim-elemani-2026",
            "yhgm-kura",
        }, ids)
        for source in sources:
            self.assertTrue(seed.valid_last_good(source, source["sourceId"]))
            for item in source["items"]:
                self.assertIsNotNone(seed.timestamp(item.get("firstSeenAt")))


if __name__ == "__main__":
    unittest.main()
