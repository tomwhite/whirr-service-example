set -x

function install_flumedemo() {
  curl -O http://cloud.github.com/downloads/cloudera/flume/flume-0.9.3.tar.gz
  tar -C /usr/local/ -zxf flume-0.9.3.tar.gz
  echo "export FLUME_CONF_DIR=/usr/local/flume-0.9.3/conf" >> /etc/profile
}
