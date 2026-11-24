import unittest
from datetime import date

from scripts.h1_schema import (
    count_verified_open_jobs,
    merge_source_items,
    preserve_first_seen,
    validate_feed,
)

TODAY = date(2026, 11, 25)


def item(**overrides):
    value = {
        "id": "job-1",
        "institution": "Örnek Kamu Kurumu",
        "title": "Personel alımı",
        "category": "Kamu",
        "contentType": "job",
        "officialSourceUrl": "https://example.gov.tr/ilan/1",
        "howToApply": {
            "method": "Resmî başvuru sayfası",
            "url": "https://example.gov.tr/basvuru",
            "instructions": "Başvuru süresi kaynak sayfasından kontrol edilir.",
        },
        "conditions": ["İlanın resmî şartları geçerlidir."],
        "applicationOpensAt": None,
        "deadline": "2026-12-08",
        "publishedAt": "2026-11-24",
        "collectedAt": "2026-11-25T10:00:00Z",
        "firstSeenAt": "2026-11-25T10:00:00Z",
        "freshnessStatus": "Doğrulandı",
        "tags": ["KPSS"],
        "sourceStatus": "fresh",
    }
    value.update(overrides)
    return value


def feed(items):
    return {
        "schemaVersion": 1,
        "collectedAt": "2026-11-25T10:00:00Z",
        "sourceId": "ornek-kaynak",
        "items": items,
    }


class H1SchemaTests(unittest.TestCase):
    def test_valid_feed_passes(self):
        self.assertEqual([], validate_feed(feed([item()]), TODAY))

    def test_null_published_and_deadline_never_become_verified(self):
        record = item(
            deadline=None,
            publishedAt=None,
            freshnessStatus="Doğrulanamadı",
        )
        self.assertEqual([], validate_feed(feed([record]), TODAY))

    def test_past_deadline_requires_expired_label_and_is_not_counted(self):
        record = item(deadline="2026-11-24", freshnessStatus="Süresi geçti")
        self.assertEqual([], validate_feed(feed([record]), TODAY))
        self.assertEqual(0, count_verified_open_jobs({"ornek-kaynak": [record]}, TODAY))
        record["freshnessStatus"] = "Doğrulandı"
        self.assertTrue(any(
            "geçmiş deadline" in error
            for error in validate_feed(feed([record]), TODAY)
        ))

    def test_invalid_category_and_content_type_are_rejected(self):
        record = item(category="Tümü", contentType="guess")
        errors = validate_feed(feed([record]), TODAY)
        self.assertTrue(any(".category:" in error for error in errors))
        self.assertTrue(any(".contentType:" in error for error in errors))

    def test_lottery_news_is_not_counted_as_job(self):
        record = item(
            contentType="lottery",
            deadline=None,
            freshnessStatus="Doğrulandı",
        )
        self.assertEqual([], validate_feed(feed([record]), TODAY))
        self.assertEqual(0, count_verified_open_jobs({"ornek-kaynak": [record]}, TODAY))

    def test_unpublished_or_not_yet_open_job_cannot_be_verified(self):
        no_publication = item(publishedAt=None)
        self.assertTrue(any(
            "publishedAt:" in error
            for error in validate_feed(feed([no_publication]), TODAY)
        ))
        not_open = item(
            applicationOpensAt="2026-11-30T09:00:00+03:00",
            collectedAt="2026-11-25T10:00:00Z",
        )
        self.assertTrue(any(
            "henüz açılmamış" in error
            for error in validate_feed(feed([not_open]), TODAY)
        ))

    def test_verified_open_job_count_requires_fresh_source_and_deadline(self):
        records = [
            item(id="fresh-open"),
            item(id="stale", sourceStatus="stale"),
            item(id="past", deadline="2026-11-24", freshnessStatus="Süresi geçti"),
            item(id="unverified", freshnessStatus="Doğrulanamadı"),
        ]
        self.assertEqual(1, count_verified_open_jobs({"source": records}, TODAY))

    def test_first_seen_is_preserved_for_same_source_item_id(self):
        previous = [{"id": "job-1", "firstSeenAt": "2026-11-20T09:00:00Z"}]
        updated = [item(firstSeenAt="2026-11-25T10:00:00Z")]
        result = preserve_first_seen(previous, updated)
        self.assertEqual("2026-11-20T09:00:00Z", result[0]["firstSeenAt"])

    def test_source_failure_marks_only_that_sources_last_good_stale(self):
        previous = {
            "source-a": [item(id="a-1")],
            "source-b": [item(id="b-1")],
        }
        fresh = {"source-b": [item(id="b-1", title="Güncel başlık")]}
        result = merge_source_items(previous, fresh, {"source-a"})
        self.assertEqual("stale", result["source-a"][0]["sourceStatus"])
        self.assertEqual("fresh", result["source-b"][0]["sourceStatus"])
        self.assertEqual("Güncel başlık", result["source-b"][0]["title"])

    def test_empty_feed_is_valid_but_invalid_json_shape_is_not(self):
        self.assertEqual([], validate_feed(feed([]), TODAY))
        self.assertTrue(validate_feed("", TODAY))

    def test_missing_timezone_and_duplicate_ids_are_rejected(self):
        bad = item(firstSeenAt="2026-11-25T10:00:00")
        errors = validate_feed(feed([bad, item()]), TODAY)
        self.assertTrue(any("firstSeenAt: saat dilimi" in error for error in errors))
        self.assertTrue(any("yinelenen kimlik" in error for error in errors))


if __name__ == "__main__":
    unittest.main()
