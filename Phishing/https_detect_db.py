from urlparse import urlparse
import shelve
import urllib2
import sys

def get_redirected_url(url):
    try:
        opener = urllib2.build_opener(urllib2.HTTPRedirectHandler)
        request = opener.open(url)
        return request.url
    except: pass

def https_detect(url):
    try: 
        redirect_to_https = False
        newurl = get_redirected_url(url)
        o = urlparse(newurl)
        if o.scheme == 'https':
            redirect_to_https = True
        return redirect_to_https
    except: 
        return False

def https_detect_db(url_database_path, start_number, end_number):
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
    https_number = 0
    for current_number in range(start_number_value, end_number_value+1):
        url = db[str(current_number)]["url"]
        print url
        result = https_detect(url)
        if result == True:
            https_number = https_number+1
            newdic = dict(https = 1)
        else:
            newdic = dict(https = 0)
        dic = db[str(current_number)]
        dic.update(newdic)
        db[str(current_number)] = dic

        current_number = current_number+1

    db.close()
    print "Totally: "+str(total)+" URLs"
    print str(https_number)+" using SSL(https)"
    return total

if __name__ == '__main__':
    if len(sys.argv)!=4:
        print 'please use "python https_detect_db.py url_database_path start_number end_number"'
        exit(1)
    else:
        url_database_path = sys.argv[1]
        start_number = sys.argv[2]
        end_number = sys.argv[3]
        total_number = https_detect_db(url_database_path, start_number, end_number)
        print "The program has finished!"
