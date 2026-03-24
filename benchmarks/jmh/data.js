window.BENCHMARK_DATA = {
  "lastUpdate": 1774383440444,
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
          "id": "a3b8f12e577af7bd93d268ca750b1276b6dc986b",
          "message": "Retry pipeline",
          "timestamp": "2026-03-23T17:42:53+02:00",
          "tree_id": "a12a91209eb43cdccd2c1fb5ae98ec9e41382775",
          "url": "https://github.com/dordor12/hbase-orm/commit/a3b8f12e577af7bd93d268ca750b1276b6dc986b"
        },
        "date": 1774282844381,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.canDeserializeString",
            "value": 0.49262064189110616,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeAsString",
            "value": 20.851091131280622,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeBigDecimal",
            "value": 18.51521271539508,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeBoolean",
            "value": 1.2266629541136114,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeDouble",
            "value": 4.794940891460569,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeFloat",
            "value": 3.196574219512669,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeInteger",
            "value": 2.4957920189152727,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeLocalDateTime",
            "value": 227.00371665043676,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeLong",
            "value": 4.484766684927558,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeShort",
            "value": 1.605430898838226,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeString",
            "value": 16.026848130398317,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeAsString",
            "value": 19.444951559232422,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeBigDecimal",
            "value": 41.426566512897445,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeBoolean",
            "value": 2.110604351265891,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeDouble",
            "value": 2.111612106410807,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeFloat",
            "value": 2.0921459394433497,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeInteger",
            "value": 2.1135829648540914,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeLocalDateTime",
            "value": 139.0163649746476,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeLong",
            "value": 2.112923968304298,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeShort",
            "value": 2.0753881307807527,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeString",
            "value": 9.914781891952094,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullReadFromPut",
            "value": 1824.9146369107946,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullReadFromResult",
            "value": 2121.5600957649663,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullWriteAsPut",
            "value": 785.9435143180716,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullWriteAsResult",
            "value": 957.1950137783883,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenMinimalReadFromResult",
            "value": 501.2239403925117,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenMinimalWriteAsPut",
            "value": 100.04679236385208,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlReadFromPut",
            "value": 230.09233352842784,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlReadFromResult",
            "value": 328.7264669377336,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlWriteAsPut",
            "value": 196.97017987893514,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeReadFromPut",
            "value": 553.1461873962058,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeReadFromResult",
            "value": 788.8606852562192,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeWriteAsPut",
            "value": 377.5845038167429,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeWriteAsResult",
            "value": 434.4546138705609,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenComposeRowKey",
            "value": 31.422699318252313,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenGetRowKey",
            "value": 23.556111729300934,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenParseRowKey",
            "value": 78.24060727574857,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlComposeRowKey",
            "value": 13.48949851243404,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlGetRowKey",
            "value": 0.9136717979580676,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlParseRowKey",
            "value": 20.640155696643998,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeComposeRowKey",
            "value": 3.9321831452055993,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeGetRowKey",
            "value": 0.8821092706660482,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeParseRowKey",
            "value": 7.178279216785325,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
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
          "id": "cf63eaed365c6fa57e11cbf549d3c6a9f60f6f4d",
          "message": "Set version to 0.1.0 for release publish\n\nCo-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>",
          "timestamp": "2026-03-23T20:24:22+02:00",
          "tree_id": "dc4d0584cd3ae367c522ef41593529d275db8161",
          "url": "https://github.com/dordor12/hbase-orm/commit/cf63eaed365c6fa57e11cbf549d3c6a9f60f6f4d"
        },
        "date": 1774292523453,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.canDeserializeString",
            "value": 0.577594328612501,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeAsString",
            "value": 19.252467470691418,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeBigDecimal",
            "value": 18.98932955557773,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeBoolean",
            "value": 1.298903664351019,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeDouble",
            "value": 4.4137173447759475,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeFloat",
            "value": 2.96474906921375,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeInteger",
            "value": 2.3118780905869776,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeLocalDateTime",
            "value": 256.7918414162513,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeLong",
            "value": 4.446771270153709,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeShort",
            "value": 1.7344778476014422,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeString",
            "value": 14.083281825392973,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeAsString",
            "value": 19.09221508592272,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeBigDecimal",
            "value": 40.4008677520456,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeBoolean",
            "value": 3.054569210059237,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeDouble",
            "value": 3.0660148842715493,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeFloat",
            "value": 3.054523588858229,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeInteger",
            "value": 3.0436577434390544,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeLocalDateTime",
            "value": 129.09416378068474,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeLong",
            "value": 3.0230566384986512,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeShort",
            "value": 2.8061363237228805,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeString",
            "value": 9.599302857360401,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullReadFromPut",
            "value": 2031.7754166495558,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullReadFromResult",
            "value": 2153.3059545883025,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullWriteAsPut",
            "value": 711.1251539955393,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullWriteAsResult",
            "value": 891.3232135497323,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenMinimalReadFromResult",
            "value": 495.44891896072903,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenMinimalWriteAsPut",
            "value": 92.8923671230529,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlReadFromPut",
            "value": 222.65075920065019,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlReadFromResult",
            "value": 339.2981902972083,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlWriteAsPut",
            "value": 162.22016375734214,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeReadFromPut",
            "value": 572.6424357308475,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeReadFromResult",
            "value": 850.9847860146846,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeWriteAsPut",
            "value": 331.3097049303476,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeWriteAsResult",
            "value": 439.66097755525624,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenComposeRowKey",
            "value": 35.15756401492008,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenGetRowKey",
            "value": 22.634085320294748,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenParseRowKey",
            "value": 71.23775885883977,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlComposeRowKey",
            "value": 12.821523331685114,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlGetRowKey",
            "value": 1.0139747779679087,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlParseRowKey",
            "value": 17.91032188983228,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeComposeRowKey",
            "value": 4.219229607260157,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeGetRowKey",
            "value": 1.0098756857235875,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeParseRowKey",
            "value": 8.17415826423831,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
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
        "date": 1774383440011,
        "tool": "jmh",
        "benches": [
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.canDeserializeString",
            "value": 0.4908197975929495,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeAsString",
            "value": 20.679508461827616,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeBigDecimal",
            "value": 18.664195714628036,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeBoolean",
            "value": 1.2275212746149025,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeDouble",
            "value": 4.822207918787295,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeFloat",
            "value": 3.206571592462423,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeInteger",
            "value": 2.5001230389390092,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeLocalDateTime",
            "value": 232.2669358227653,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeLong",
            "value": 4.500276229321373,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeShort",
            "value": 1.6101329491955072,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.deserializeString",
            "value": 16.012912447579104,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeAsString",
            "value": 19.529069408649374,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeBigDecimal",
            "value": 41.178062524329064,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeBoolean",
            "value": 2.0988284205237933,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeDouble",
            "value": 2.0990746091057058,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeFloat",
            "value": 2.0822692829205387,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeInteger",
            "value": 2.105701072188664,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeLocalDateTime",
            "value": 138.3582865069842,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeLong",
            "value": 2.0986562040903194,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeShort",
            "value": 2.079191519561492,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.CodecBenchmark.serializeString",
            "value": 9.957976615206563,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullReadFromPut",
            "value": 1839.189067591574,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullReadFromResult",
            "value": 2116.1496606137757,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullWriteAsPut",
            "value": 800.5237025000314,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenFullWriteAsResult",
            "value": 972.1904289250925,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenMinimalReadFromResult",
            "value": 496.54759881099875,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.citizenMinimalWriteAsPut",
            "value": 100.43721493492028,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlReadFromPut",
            "value": 228.7908573191164,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlReadFromResult",
            "value": 329.89315916765077,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.crawlWriteAsPut",
            "value": 194.90378733612746,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeReadFromPut",
            "value": 555.6173719063526,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeReadFromResult",
            "value": 822.0964676339155,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeWriteAsPut",
            "value": 400.64664248612604,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.MapperBenchmark.employeeWriteAsResult",
            "value": 480.353983145867,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenComposeRowKey",
            "value": 31.8321071625568,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenGetRowKey",
            "value": 23.44232086733386,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.citizenParseRowKey",
            "value": 80.71677905408329,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlComposeRowKey",
            "value": 13.529722648647692,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlGetRowKey",
            "value": 0.8847377813277196,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.crawlParseRowKey",
            "value": 20.7444984857795,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeComposeRowKey",
            "value": 3.9439576980667113,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeGetRowKey",
            "value": 0.8728233417031149,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "io.github.dordor12.hbase.orm.bench.RowKeyBenchmark.employeeParseRowKey",
            "value": 7.192567751387348,
            "unit": "ns/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}