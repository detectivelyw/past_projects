from urlparse import urlparse
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

if __name__ == '__main__':
    if len(sys.argv)!=2:
        print 'please use "python https_detect.py url"'
        exit(1)
    else:
        url = sys.argv[1]
        result = https_detect(url)
        print "URL: "+url+" using SSL: "+str(result)