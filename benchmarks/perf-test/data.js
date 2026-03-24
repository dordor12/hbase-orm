window.BENCHMARK_DATA = {
  "lastUpdate": 1774381033737,
  "repoUrl": "https://github.com/dordor12/hbase-orm",
  "entries": {
    "Benchmark": [
      {
        "commit": {
          "author": {
            "email": "dor.amid@taboola.com",
            "name": "Dor Amid"
          },
          "committer": {
            "email": "dor.amid@taboola.com",
            "name": "Dor Amid"
          },
          "distinct": true,
          "id": "1c893e3a05d1e8b4dd4fecbaab9c01223229564a",
          "message": "Optimize Testcontainers CI with pre-built GHCR image and shared container\n\n- Publish HBase test image to ghcr.io via new docker-publish workflow\n- Use HBASE_TEST_IMAGE env var to pull pre-built image in CI (falls back\n  to building from Dockerfile locally)\n- Share single HBase container across integration test classes via\n  SharedHBaseContainer singleton (eliminates duplicate container startup)\n- Add docker pull step in CI workflows to warm cache before tests\n\nCo-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>",
          "timestamp": "2026-03-24T21:24:57+02:00",
          "tree_id": "5c76490657bd948c431099493ec273f6d5adb313",
          "url": "https://github.com/dordor12/hbase-orm/commit/1c893e3a05d1e8b4dd4fecbaab9c01223229564a"
        },
        "date": 1774380658121,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "compare.sync.single_put_get (p50)",
            "value": 1671.17,
            "unit": "us",
            "extra": "p95=3381.7us p99=4537.3us mean=1892.1us throughput=528.4ops/s"
          },
          {
            "name": "compare.async.single_put_get (p50)",
            "value": 1213.87,
            "unit": "us",
            "extra": "p95=1573.3us p99=2292.9us mean=1265.8us throughput=789.8ops/s"
          },
          {
            "name": "compare.sync.bulk_put_100 (p50)",
            "value": 2827.86,
            "unit": "us",
            "extra": "p95=5557.2us p99=6253.9us mean=3360.4us throughput=297.6ops/s"
          },
          {
            "name": "compare.async.bulk_put_100 (p50)",
            "value": 19354.04,
            "unit": "us",
            "extra": "p95=30236.1us p99=42077.3us mean=21164.1us throughput=47.2ops/s"
          },
          {
            "name": "compare.sync.prefix_scan_100 (p50)",
            "value": 1709.41,
            "unit": "us",
            "extra": "p95=3107.1us p99=3121.3us mean=1854.8us throughput=539.0ops/s"
          },
          {
            "name": "compare.async.prefix_scan_100 (p50)",
            "value": 1639.18,
            "unit": "us",
            "extra": "p95=6157.9us p99=6160.0us mean=2046.6us throughput=488.5ops/s"
          },
          {
            "name": "compare.sync.bulk_get_100 (p50)",
            "value": 7525.99,
            "unit": "us",
            "extra": "p95=12138.2us p99=15247.7us mean=8051.6us throughput=124.2ops/s"
          },
          {
            "name": "compare.async.bulk_get_100 (p50)",
            "value": 4427.84,
            "unit": "us",
            "extra": "p95=15472.1us p99=30641.1us mean=6218.1us throughput=160.8ops/s"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "dor.amid@taboola.com",
            "name": "Dor Amid"
          },
          "committer": {
            "email": "dor.amid@taboola.com",
            "name": "Dor Amid"
          },
          "distinct": true,
          "id": "35b76280908c4f4698c843374894f9fba0761af2",
          "message": "Add GHCR login before pulling HBase test image in CI\n\nThe GHCR package requires authentication to pull. Add docker/login-action\nstep before the pull in both integration-test and perf-test workflows.\n\nCo-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>",
          "timestamp": "2026-03-24T21:34:37+02:00",
          "tree_id": "0d0270c4d90754b6c188f4f03e9fbdcb6c8582f4",
          "url": "https://github.com/dordor12/hbase-orm/commit/35b76280908c4f4698c843374894f9fba0761af2"
        },
        "date": 1774381033218,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "compare.sync.single_put_get (p50)",
            "value": 1461.52,
            "unit": "us",
            "extra": "p95=2180.0us p99=3017.2us mean=1526.8us throughput=654.7ops/s"
          },
          {
            "name": "compare.async.single_put_get (p50)",
            "value": 956.63,
            "unit": "us",
            "extra": "p95=1390.7us p99=3058.3us mean=1041.6us throughput=959.6ops/s"
          },
          {
            "name": "compare.sync.bulk_put_100 (p50)",
            "value": 2937.32,
            "unit": "us",
            "extra": "p95=4878.2us p99=5271.5us mean=3253.7us throughput=307.3ops/s"
          },
          {
            "name": "compare.async.bulk_put_100 (p50)",
            "value": 13175.18,
            "unit": "us",
            "extra": "p95=23399.3us p99=23747.6us mean=14979.7us throughput=66.8ops/s"
          },
          {
            "name": "compare.sync.prefix_scan_100 (p50)",
            "value": 1800.56,
            "unit": "us",
            "extra": "p95=3010.8us p99=3516.7us mean=2074.0us throughput=482.1ops/s"
          },
          {
            "name": "compare.async.prefix_scan_100 (p50)",
            "value": 1313.58,
            "unit": "us",
            "extra": "p95=1972.7us p99=2105.2us mean=1399.0us throughput=714.6ops/s"
          },
          {
            "name": "compare.sync.bulk_get_100 (p50)",
            "value": 3837.18,
            "unit": "us",
            "extra": "p95=10729.6us p99=11459.4us mean=4615.5us throughput=216.6ops/s"
          },
          {
            "name": "compare.async.bulk_get_100 (p50)",
            "value": 4731.93,
            "unit": "us",
            "extra": "p95=13089.4us p99=22810.8us mean=6109.8us throughput=163.7ops/s"
          }
        ]
      }
    ]
  }
}