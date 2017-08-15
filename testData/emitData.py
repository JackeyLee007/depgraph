import csv
import datetime
import time
from kafka import KafkaProducer
from collections import namedtuple



KAFKA_SERVERS = ['localhost:9092']


def passData(dataFile):
    producer = KafkaProducer(bootstrap_servers = KAFKA_SERVERS)

    with open(dataFile, 'r') as df:
        df_csv = csv.reader(df)

        header = next(df_csv)
        Log = namedtuple('Log',header)

        for line in df_csv:
            log = Log(*line)

            if log.source_type == "pmstoudasystem":
                now = datetime.datetime.now()
                line[8] = now.isoformat()
                producer.send("depcalls", value = ','.join(line).encode("utf8"))
                print(line)

                time.sleep(1)
                line[1] = "_"+line[1]
                line[6] = "pmstoudaproxy"
                line[8] = (now + datetime.timedelta(seconds=1)).isoformat()
                producer.send("depcalls", value = ','.join(line).encode("utf8"))
                print(line)


if __name__ == '__main__':
    passData('./resource/fake_1.csv')