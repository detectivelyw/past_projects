from BeautifulSoup import BeautifulSoup
import os
import sys

def html_analysis_average(html_path):
    ouput_filename = html_path+"-links.txt"
    f = open(ouput_filename, 'w')
    
    count_file_number = 0
    count_externaljavascript = 0
    count_cssexternallink = 0
    count_login_number = 0
    contain_login_number = 0

    tofinda = 'www'
    tofindb = 'com'
    tofindc = 'log'
    tofindd = 'sign'
    tofinde = 'register'

    for filename in os.listdir(html_path):
        count_page_valid = 0
        count_externallink = 0
        count_internallink = 0
        count_file_number = count_file_number+1
        print filename
        try: 
            soup = BeautifulSoup(file(html_path+filename))

            for link in soup.findAll('a'):
                if (link.get('href')!= None):
                    if tofinda in link.get('href') or tofindb in link.get('href'):
                        count_externallink = count_externallink + 1
                        count_page_valid = count_page_valid + 1
                    if tofinda not in link.get('href') and tofindb not in link.get('href'):
                        count_internallink = count_internallink + 1
                        count_page_valid = count_page_valid + 1
                if (link.get('src')!= None):
                    if tofinda in link.get('src') or tofindb in link.get('src'):
                        count_externallink = count_externallink + 1
                        count_page_valid = count_page_valid + 1
                    if tofinda not in link.get('src') or tofindb not in link.get('src'):
                        count_internallink = count_internallink + 1
                        count_page_valid = count_page_valid + 1
            for link in soup.findAll('img'):
                if(link.get('src')!=None):
                    if tofinda in link.get('src') or tofindb in link.get('src'):
                        count_externallink = count_externallink + 1
                        count_page_valid = count_page_valid + 1
                    if tofinda not in link.get('src') or tofindb not in link.get('src'):
                        count_internallink = count_internallink + 1
                        count_page_valid = count_page_valid + 1
            for link in soup.findAll('img'):
                if(link.get('href')!=None):
                    if tofinda in link.get('href') or tofindb in link.get('href'):
                        count_externallink = count_externallink + 1
                        count_page_valid = count_page_valid + 1
                    if tofinda not in link.get('href') or tofindb not in link.get('href'):
                        count_internallink = count_internallink + 1
                        count_page_valid = count_page_valid + 1
            for link in soup.findAll('div'):
                if (link.get('href')!= None):
                    if tofinda in link.get('href') or tofindb in link.get('href'):
                        count_externallink = count_externallink + 1
                        count_page_valid = count_page_valid + 1
                    if tofinda not in link.get('href') and tofindb not in link.get('href'):
                        count_internallink = count_internallink + 1
                        count_page_valid = count_page_valid + 1
                if (link.get('src')!= None):
                    if tofinda in link.get('src') or tofindb in link.get('src'):
                        count_externallink = count_externallink + 1
                        count_page_valid = count_page_valid + 1
                    if tofinda not in link.get('src') or tofindb not in link.get('src'):
                        count_internallink = count_internallink + 1
                        count_page_valid = count_page_valid + 1

            for link in soup.findAll('script'):
                if(link.get('src')!= None):
                    if tofinda in link.get('src') or tofindb in link.get('src'):
                        count_externaljavascript = count_externaljavascript + 1
                        count_page_valid = count_page_valid + 1
                if(link.get('href')!= None):
                    if tofinda in link.get('href') or tofindb in link.get('href'):
                        count_externaljavascript = count_externaljavascript + 1  
                        count_page_valid = count_page_valid + 1              

            for link in soup.findAll('link'):
                if(link.get('href')!=None):
                    if tofinda in link.get('href') or tofindb in link.get('href'):
                        count_cssexternallink = count_cssexternallink + 1
                        count_page_valid = count_page_valid + 1
                if(link.get('src')!=None):
                    if tofinda in link.get('src') or tofindb in link.get('src'):
                        count_cssexternallink = count_cssexternallink + 1 
                        count_page_valid = count_page_valid + 1               

            contain_login = False
            for link in soup.findAll('form'):
                if (link.get('name')!=None):
                    if tofindc in link.get('name') or tofindd in link.get('name') or tofinde in link.get('name'):
                        count_login_number = count_login_number + 1
                        count_page_valid = count_page_valid + 1
                        contain_login = True
                elif (link.get('action')!= None):
                    if tofindc in link.get('action') or tofindd in link.get('action') or tofinde in link.get('action'):
                        count_login_number = count_login_number + 1
                        count_page_valid = count_page_valid + 1
                        contain_login = True
                   
            for link in soup.findAll('div'):
                if (link.get('name')!=None):
                    if tofindc in link.get('name') or tofindd in link.get('name') or tofinde in link.get('name'):
                        count_login_number = count_login_number + 1
                        count_page_valid = count_page_valid + 1
                        contain_login = True
                elif (link.get('action')!= None):
                    if tofindc in link.get('action') or tofindd in link.get('action') or tofinde in link.get('action'):
                        count_login_number = count_login_number + 1
                        count_page_valid = count_page_valid + 1
                        contain_login = True
            if contain_login == True:
                contain_login_number = contain_login_number + 1
            if count_page_valid != 0:
                f.write(str(count_externallink-count_internallink)+", ")
        except: pass
    f.close()

if __name__ == '__main__':
    if len(sys.argv)!=2:
        print 'please use "python externallink_internallink_cdf.py html_path"'
        exit(1)
    else:
        html_path = sys.argv[1]
        html_analysis_average(html_path)
        print "The program has finished!"