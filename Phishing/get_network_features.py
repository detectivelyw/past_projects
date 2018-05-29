# get network features for ulrs in the given database
from urlparse import urlparse
from ip import loc_asn_domain
import socket
import geoip
import shelve
import sys

def get_ip_address(hostname):
    ip = socket.gethostbyname(hostname)
    return ip

# given an URL, this function will return related network features:
# for example:
# URL: http://www.cwi.nl:80/%7Eguido/Python.html
#{'country': 'NL', 'domain': 'nl', 'hostname': 'www.cwi.nl', 'host_ip': '192.16.191.43', 'asn': '1888'}
def get_url_feature(url_input):
    network_features = {}
    hostname = urlparse(url_input).hostname
    url = urlparse(url_input)[0]+"://"+urlparse(url_input).hostname
    network_features['hostname'] = hostname
    try:
        ip = socket.gethostbyname(hostname)
    except:
        ip = ""
    try:
        if 'http' in url and url[-1] =='/':
            network_features['host_ip'] = socket.gethostbyname(url[url.find(':')+3:-1])
        elif 'http' in url:
            network_features['host_ip'] = socket.gethostbyname(url[url.find(':')+3:])
        else:
            network_features['host_ip'] = socket.gethostbyname(url)
    except:
        network_features['host_ip'] = ''
        
    try:
        lad = loc_asn_domain(network_features['host_ip'])
        network_features['country'] = geoip.country(ip)
        network_features['asn'] = lad[2]
        network_features['domain'] = lad[1]        
    except:
        network_features['country'] = ''
        network_features['asn'] = ''
        network_features['domain'] = ''   

    return network_features

def save_network_features(url_database_path, start_number, end_number):
    db = shelve.open(url_database_path)
    start_number_value = int(start_number)
    current_number = start_number_value
    number_limit = db["total_number"]
    if end_number == "end":
        end_number_value = number_limit-1
    else:
        end_number_value = int(end_number)
    if end_number_value >= number_limit:
        end_number_value = number_limit-1
    total = end_number_value-start_number_value+1
    db.close()
    for current_number in range(start_number_value, end_number_value+1):
        db = shelve.open(url_database_path)
        url_input = db[str(current_number)]["url"]
        print str(current_number)+": "+url_input
        network_features_result = get_url_feature(url_input)
        newdic = dict(network_features = network_features_result)
        dic = db[str(current_number)]
        dic.update(newdic)
        db[str(current_number)] = dic

        current_number = current_number+1
        db.close()

    return total

if __name__ == '__main__':
    if len(sys.argv)!=4:
        print 'please use "python get_network_feature.py url_database_path start_number end_number"'
        print 'the script will get and save the network features for the ulrs from start_number to end_number in the database.'
        print 'if you want to save to the last url in the database, simply input end_number as "end". The start_number should be an integer.'
        exit(1)
    else:
        url_database_path = sys.argv[1]
        start_number = sys.argv[2]
        end_number = sys.argv[3]
        total_number = save_network_features(url_database_path, start_number, end_number)
        print "The program has finished!"
        print "Get net work features for totally "+str(total_number)+" urls successfully!"
