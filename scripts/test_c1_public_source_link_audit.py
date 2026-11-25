import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import c1_public_source_link_audit as audit


class LinkAuditTests(unittest.TestCase):
    def test_robots_404_never_requests_page(self):
        calls = []

        def requester(url, limit):
            calls.append(url)
            return {"status": 404, "body": b"", "error": "HTTP_404"}

        result = audit.audit_page(
            "https://example.gov.tr/notice",
            ["example.gov.tr"],
            requester,
        )
        self.assertFalse(result["page_requested"])
        self.assertIsNone(result["allowed"])
        self.assertEqual(["https://example.gov.tr/robots.txt"], calls)

    def test_robots_disallow_never_requests_page(self):
        calls = []

        def requester(url, limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return {"status": 200, "body": b"User-agent: *\nDisallow: /notice", "error": None}
            return {"status": 200, "body": b"<html>must not fetch</html>", "error": None}

        result = audit.audit_page(
            "https://example.gov.tr/notice",
            ["example.gov.tr"],
            requester,
        )
        self.assertFalse(result["page_requested"])
        self.assertIs(result["allowed"], False)
        self.assertEqual(["https://example.gov.tr/robots.txt"], calls)

    def test_allowed_page_extracts_only_allowlisted_official_links(self):
        calls = []

        def requester(url, limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return {"status": 200, "body": b"User-agent: *\nAllow: /", "error": None}
            body = (
                b'<html><head><title>Official Notice</title>'
                b'<meta property="article:published_time" content="2026-11-20"></head>'
                b'<body><p>Son Basvuru 04.12.2026</p>'
                b'<a href="/files/notice.pdf">PDF</a>'
                b'<a href="https://evil.example/steal.pdf">outside</a>'
                b'<iframe src="https://cdn.example.gov.tr/embedded.pdf"></iframe>'
                b'</body></html>'
            )
            return {"status": 200, "body": body, "error": None}

        result = audit.audit_page(
            "https://www.example.gov.tr/notice",
            ["www.example.gov.tr", "cdn.example.gov.tr"],
            requester,
        )
        self.assertTrue(result["page_requested"])
        self.assertEqual("Official Notice", result["title"])
        self.assertEqual("2026-11-20", result["meta"]["article:published_time"])
        urls = [item["url"] for item in result["official_links"]]
        self.assertIn("https://www.example.gov.tr/files/notice.pdf", urls)
        self.assertIn("https://cdn.example.gov.tr/embedded.pdf", urls)
        self.assertNotIn("https://evil.example/steal.pdf", urls)
        self.assertEqual(
            ["https://www.example.gov.tr/robots.txt", "https://www.example.gov.tr/notice"],
            calls,
        )


    def test_parser_ignores_script_and_style_content(self):
        body = (
            b'<html><head><title>Notice</title><style>.secret { display:none }</style>'
            b'<script>var fake="not page text";</script></head>'
            b'<body><p>Real announcement</p></body></html>'
        )
        result = audit.parse_page(body, "https://example.gov.tr/notice", ["example.gov.tr"])
        self.assertIn("Real announcement", result["text_excerpt"])
        self.assertNotIn("fake", result["text_excerpt"])
        self.assertNotIn("display:none", result["text_excerpt"])


    def test_page_403_is_reported_without_redirect_or_retry(self):
        calls = []

        def requester(url, limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return {"status": 200, "body": b"User-agent: *\nAllow: /", "error": None}
            return {"status": 403, "body": b"", "error": "HTTP_403"}

        result = audit.audit_page("https://example.gov.tr/notice", ["example.gov.tr"], requester)
        self.assertFalse(result["page_requested"])
        self.assertEqual("HTTP_403", result["page_error"])
        self.assertEqual(2, len(calls))

    def test_redirect_is_not_followed(self):
        calls = []

        def requester(url, limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return {"status": 200, "body": b"User-agent: *\nAllow: /", "error": None}
            return {"status": 302, "body": b"", "error": "HTTP_302"}

        result = audit.audit_page("https://example.gov.tr/notice", ["example.gov.tr"], requester)
        self.assertFalse(result["page_requested"])
        self.assertEqual(2, len(calls))

    def test_page_timeout_is_reported(self):
        calls = []

        def requester(url, limit):
            calls.append(url)
            if url.endswith("/robots.txt"):
                return {"status": 200, "body": b"User-agent: *\nAllow: /", "error": None}
            return {"status": None, "body": b"", "error": "TIMEOUT"}

        result = audit.audit_page("https://example.gov.tr/notice", ["example.gov.tr"], requester)
        self.assertFalse(result["page_requested"])
        self.assertEqual("TIMEOUT", result["page_error"])
        self.assertEqual(2, len(calls))


    def test_robots_500_and_empty_response_never_request_page(self):
        for response in (
            {"status": 500, "body": b"", "error": "HTTP_500"},
            {"status": 200, "body": b"", "error": None},
        ):
            calls = []

            def requester(url, limit, response=response):
                calls.append(url)
                return response

            result = audit.audit_page("https://example.gov.tr/notice", ["example.gov.tr"], requester)
            self.assertFalse(result["page_requested"])
            self.assertIsNone(result["allowed"])
            self.assertEqual(1, len(calls))
            self.assertTrue(calls[0].endswith("/robots.txt"))

    def test_page_500_and_empty_response_are_reported(self):
        for response in (
            {"status": 500, "body": b"", "error": "HTTP_500"},
            {"status": 200, "body": b"", "error": None},
        ):
            calls = []

            def requester(url, limit, response=response):
                calls.append(url)
                if url.endswith("/robots.txt"):
                    return {"status": 200, "body": b"User-agent: *\\nAllow: /", "error": None}
                return response

            result = audit.audit_page("https://example.gov.tr/notice", ["example.gov.tr"], requester)
            self.assertFalse(result["page_requested"])
            self.assertIn("page_error", result)
            self.assertEqual(2, len(calls))


if __name__ == "__main__":
    unittest.main()
