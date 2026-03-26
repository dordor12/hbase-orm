window.BENCHMARK_DATA = {
  "lastUpdate": 1774529051052,
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
          "id": "00e45707aee84aed39454d0d37876c55054a1f05",
          "message": "Re-run pipeline with pre-built HBase image",
          "timestamp": "2026-03-24T21:39:25+02:00",
          "tree_id": "0d0270c4d90754b6c188f4f03e9fbdcb6c8582f4",
          "url": "https://github.com/dordor12/hbase-orm/commit/00e45707aee84aed39454d0d37876c55054a1f05"
        },
        "date": 1774381326707,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "compare.sync.single_put_get (p50)",
            "value": 1674.27,
            "unit": "us",
            "extra": "p95=2497.7us p99=3336.9us mean=1736.7us throughput=575.6ops/s"
          },
          {
            "name": "compare.async.single_put_get (p50)",
            "value": 940.72,
            "unit": "us",
            "extra": "p95=1570.7us p99=3409.0us mean=1036.5us throughput=964.3ops/s"
          },
          {
            "name": "compare.sync.bulk_put_100 (p50)",
            "value": 2851.95,
            "unit": "us",
            "extra": "p95=4172.5us p99=4958.6us mean=3093.6us throughput=323.2ops/s"
          },
          {
            "name": "compare.async.bulk_put_100 (p50)",
            "value": 14150.85,
            "unit": "us",
            "extra": "p95=18293.0us p99=20957.0us mean=14324.1us throughput=69.8ops/s"
          },
          {
            "name": "compare.sync.prefix_scan_100 (p50)",
            "value": 2199.77,
            "unit": "us",
            "extra": "p95=4396.4us p99=5995.9us mean=2634.6us throughput=379.5ops/s"
          },
          {
            "name": "compare.async.prefix_scan_100 (p50)",
            "value": 1412.09,
            "unit": "us",
            "extra": "p95=1813.0us p99=1946.8us mean=1363.8us throughput=733.1ops/s"
          },
          {
            "name": "compare.sync.bulk_get_100 (p50)",
            "value": 4434.45,
            "unit": "us",
            "extra": "p95=6871.4us p99=10001.9us mean=4643.1us throughput=215.3ops/s"
          },
          {
            "name": "compare.async.bulk_get_100 (p50)",
            "value": 3721.84,
            "unit": "us",
            "extra": "p95=5465.7us p99=5787.6us mean=3918.8us throughput=255.1ops/s"
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
          "id": "6c90c8f064860725df20ba7c5999aa23bb901b4c",
          "message": "Migrate Maven Central publishing from nexus-publish to VannikTech plugin\n\nOSSRH staging API returns 402 (deprecated/sunset). Switch to\ncom.vanniktech.maven.publish which uses the new Central Portal API.\nAdds retry logic and GPG_SIGNING_KEY_ID secret.\n\nCo-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>",
          "timestamp": "2026-03-25T10:43:54+02:00",
          "tree_id": "f829264962a7fc4fc7b75f9fa1531d3073248314",
          "url": "https://github.com/dordor12/hbase-orm/commit/6c90c8f064860725df20ba7c5999aa23bb901b4c"
        },
        "date": 1774428409264,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "compare.sync.single_put_get (p50)",
            "value": 1695.5,
            "unit": "us",
            "extra": "p95=2486.5us p99=3784.2us mean=1752.5us throughput=570.4ops/s"
          },
          {
            "name": "compare.async.single_put_get (p50)",
            "value": 1224.4,
            "unit": "us",
            "extra": "p95=1796.4us p99=3044.2us mean=1307.8us throughput=764.3ops/s"
          },
          {
            "name": "compare.sync.bulk_put_100 (p50)",
            "value": 3301.01,
            "unit": "us",
            "extra": "p95=4847.2us p99=5721.6us mean=3493.5us throughput=286.2ops/s"
          },
          {
            "name": "compare.async.bulk_put_100 (p50)",
            "value": 17137.46,
            "unit": "us",
            "extra": "p95=23295.4us p99=24267.7us mean=17781.5us throughput=56.2ops/s"
          },
          {
            "name": "compare.sync.prefix_scan_100 (p50)",
            "value": 1855.74,
            "unit": "us",
            "extra": "p95=2359.4us p99=3648.5us mean=1945.7us throughput=513.8ops/s"
          },
          {
            "name": "compare.async.prefix_scan_100 (p50)",
            "value": 1695.37,
            "unit": "us",
            "extra": "p95=4722.3us p99=5525.6us mean=2123.4us throughput=470.8ops/s"
          },
          {
            "name": "compare.sync.bulk_get_100 (p50)",
            "value": 5551.83,
            "unit": "us",
            "extra": "p95=7036.5us p99=7286.3us mean=5534.5us throughput=180.7ops/s"
          },
          {
            "name": "compare.async.bulk_get_100 (p50)",
            "value": 4511.46,
            "unit": "us",
            "extra": "p95=5471.8us p99=5697.6us mean=4546.9us throughput=219.9ops/s"
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
          "id": "3860fb62107ad34b8c4c90859e9c120c5c652766",
          "message": "Migrate hbase-orm-processor publishing to VannikTech plugin\n\nSame migration as hbase-orm-api — use com.vanniktech.maven.publish\nfor Central Portal compatibility.\n\nCo-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>",
          "timestamp": "2026-03-26T14:14:00+02:00",
          "tree_id": "efd6db4e46453afdd07e6abbdb31fe3f91172e71",
          "url": "https://github.com/dordor12/hbase-orm/commit/3860fb62107ad34b8c4c90859e9c120c5c652766"
        },
        "date": 1774527408589,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "compare.sync.single_put_get (p50)",
            "value": 1561.23,
            "unit": "us",
            "extra": "p95=2568.6us p99=3688.8us mean=1637.6us throughput=610.4ops/s"
          },
          {
            "name": "compare.async.single_put_get (p50)",
            "value": 1082.05,
            "unit": "us",
            "extra": "p95=1860.7us p99=3408.1us mean=1191.8us throughput=838.7ops/s"
          },
          {
            "name": "compare.sync.bulk_put_100 (p50)",
            "value": 2723.68,
            "unit": "us",
            "extra": "p95=5348.2us p99=8195.1us mean=3508.2us throughput=285.0ops/s"
          },
          {
            "name": "compare.async.bulk_put_100 (p50)",
            "value": 15277.19,
            "unit": "us",
            "extra": "p95=20640.0us p99=27940.9us mean=15906.1us throughput=62.9ops/s"
          },
          {
            "name": "compare.sync.prefix_scan_100 (p50)",
            "value": 1907.62,
            "unit": "us",
            "extra": "p95=4189.4us p99=4199.7us mean=2241.8us throughput=446.0ops/s"
          },
          {
            "name": "compare.async.prefix_scan_100 (p50)",
            "value": 1350.45,
            "unit": "us",
            "extra": "p95=1728.9us p99=2071.6us mean=1403.6us throughput=712.3ops/s"
          },
          {
            "name": "compare.sync.bulk_get_100 (p50)",
            "value": 4883.52,
            "unit": "us",
            "extra": "p95=7313.9us p99=9108.2us mean=5169.1us throughput=193.4ops/s"
          },
          {
            "name": "compare.async.bulk_get_100 (p50)",
            "value": 3974.13,
            "unit": "us",
            "extra": "p95=7329.0us p99=8196.5us mean=4602.8us throughput=217.2ops/s"
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
          "id": "80ddb2d40fa051c80e75684b6c3174fda2d890a1",
          "message": "Replace Javadoc with Dokka for modern API documentation\n\nProduces searchable HTML with dark mode toggle, same style as\nKotlin stdlib docs. Uses dokkaHtmlMultiModule for aggregated\nmulti-module output deployed to GitHub Pages.\n\nCo-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>",
          "timestamp": "2026-03-26T14:40:57+02:00",
          "tree_id": "8d758eb50d5c75df63e45ba730439bdd598471a0",
          "url": "https://github.com/dordor12/hbase-orm/commit/80ddb2d40fa051c80e75684b6c3174fda2d890a1"
        },
        "date": 1774529050503,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "compare.sync.single_put_get (p50)",
            "value": 1622.61,
            "unit": "us",
            "extra": "p95=2285.4us p99=3470.1us mean=1655.3us throughput=603.9ops/s"
          },
          {
            "name": "compare.async.single_put_get (p50)",
            "value": 1141.48,
            "unit": "us",
            "extra": "p95=2524.0us p99=4407.9us mean=1302.9us throughput=767.1ops/s"
          },
          {
            "name": "compare.sync.bulk_put_100 (p50)",
            "value": 3021.38,
            "unit": "us",
            "extra": "p95=4712.9us p99=7140.0us mean=3466.9us throughput=288.4ops/s"
          },
          {
            "name": "compare.async.bulk_put_100 (p50)",
            "value": 15078.68,
            "unit": "us",
            "extra": "p95=18048.4us p99=24231.2us mean=15349.4us throughput=65.2ops/s"
          },
          {
            "name": "compare.sync.prefix_scan_100 (p50)",
            "value": 3032.59,
            "unit": "us",
            "extra": "p95=5513.5us p99=9600.6us mean=3254.4us throughput=307.2ops/s"
          },
          {
            "name": "compare.async.prefix_scan_100 (p50)",
            "value": 1335.62,
            "unit": "us",
            "extra": "p95=1536.7us p99=1613.6us mean=1364.5us throughput=732.7ops/s"
          },
          {
            "name": "compare.sync.bulk_get_100 (p50)",
            "value": 5140.73,
            "unit": "us",
            "extra": "p95=10461.6us p99=11555.3us mean=6294.9us throughput=158.8ops/s"
          },
          {
            "name": "compare.async.bulk_get_100 (p50)",
            "value": 3296.21,
            "unit": "us",
            "extra": "p95=3719.2us p99=5118.3us mean=3380.4us throughput=295.8ops/s"
          }
        ]
      }
    ]
  }
}