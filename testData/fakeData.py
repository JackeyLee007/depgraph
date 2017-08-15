import csv
from collections import namedtuple

SYSTEM = 'pmstoudasystem'
PROXY = 'pmstoudaproxy'


def fakeData(dataFile, fakeFile):
    with open(dataFile, 'r', encoding='utf-8') as df,\
            open(fakeFile, 'w') as ff:
        df_csv = csv.reader(df)
        ff_csv = csv.writer(ff)
        headers = next(df_csv)
        Log = namedtuple('Log', headers)

        ff_csv.writerow(headers)

        for line in df_csv:
            log = Log(*line)
            # print(log.transcode + "|" + log.source_type + '|'+log.globalseqno )


            ff_csv.writerow(log)
            # if log.source_type == SYSTEM:
            #     newLog = log._replace(source_type = PROXY)
            # # elif log.source_type == PROXY:
            # #     newLog = log._replace(source_type = SYSTEM)
            # else:
            #     pass
            #
            # ff_csv.writerow(newLog)


if __name__ == '__main__':
    # conn = happybase.Connection(host='localhost')
    # loadData(conn)

    fakeData('./resource/export0808_1.csv', './resource/fake_1.csv')