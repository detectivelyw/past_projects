# get whois information for URLs in the given database
import shelve
import sys
import pywhois

# get all information related to the given domain name
def get_whois_information(domain_name):
    try:
        w = pywhois.whois(domain_name)
    except: w = " "
    return w

def save_whois_information(url_database_path, start_number, end_number):
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
        result = get_whois_information(url_input)
        whois_information_value = {}
        try:
            whois_information_value["creation_date"] = result.creation_date
            whois_information_value["domain_name"] = result.domain_name
            whois_information_value["emails"] = result.emails
            whois_information_value["expiration_date"] = result.expiration_date
            whois_information_value["name_servers"] = result.name_servers
            whois_information_value["referral_url"] = result.referral_url
            whois_information_value["registrar"] = result.registrar
            whois_information_value["status"] = result.status
            whois_information_value["updated_date"] = result.updated_date
            whois_information_value["whois_server"] = result.whois_server
        except: pass
        
        newdic = dict(whois_information = whois_information_value)
        dic = db[str(current_number)]
        dic.update(newdic)
        db[str(current_number)] = dic

        current_number = current_number+1
        db.close()

    return total

if __name__ == '__main__':
    if len(sys.argv)!=4:
        print 'please use "python get_whois_information_db.py url_database_path start_number end_number"'
        print 'the script will get and save the whois information for the ulrs from start_number to end_number in the database.'
        print 'if you want to save to the last url in the database, simply input end_number as "end". The start_number should be an integer.'
        exit(1)
    else:
        url_database_path = sys.argv[1]
        start_number = sys.argv[2]
        end_number = sys.argv[3]
        total_number = save_whois_information(url_database_path, start_number, end_number)
        print "The program has finished!"
        print "Get whois information for totally "+str(total_number)+" urls successfully!"
