set -x

function configure_flumedemo_master() {
  FLUME_CONF_DIR=/usr/local/flume-0.9.3/conf \
    nohup /usr/local/flume-0.9.3/bin/flume master > /var/log/flume.log 2>&1 &
}
