FROM eclipse-temurin:11-jre

ENV HBASE_VERSION=2.4.18
ENV HBASE_HOME=/opt/hbase

RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    procps \
    && rm -rf /var/lib/apt/lists/*

RUN wget -q "https://archive.apache.org/dist/hbase/${HBASE_VERSION}/hbase-${HBASE_VERSION}-bin.tar.gz" \
    -O /tmp/hbase.tar.gz \
    && mkdir -p ${HBASE_HOME} \
    && tar -xzf /tmp/hbase.tar.gz -C ${HBASE_HOME} --strip-components=1 \
    && rm /tmp/hbase.tar.gz

COPY hbase-site.xml ${HBASE_HOME}/conf/hbase-site.xml

ENV PATH="${HBASE_HOME}/bin:${PATH}"
ENV JAVA_HOME=/opt/java/openjdk

# ZooKeeper, HBase Master, HBase RegionServer, Master Web UI
EXPOSE 2181 16000 16020 16010

CMD ["sh", "-c", "start-hbase.sh && tail -f /opt/hbase/logs/*"]
