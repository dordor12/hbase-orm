window.BENCHMARK_DATA = {
  "lastUpdate": 1774282845181,
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
      }
    ]
  }
}