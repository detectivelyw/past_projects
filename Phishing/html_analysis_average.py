from BeautifulSoup import BeautifulSoup
import os
import sys

def html_analysis_average(html_path, target):
    count_file_number = 0
    count_externallink = 0
    count_internallink = 0
    count_externaljavascript = 0
    count_cssexternallink = 0
    count_login_number = 0
    contain_login_number = 0
    target_title_number = 0
    contain_title_number = 0
    tofinda = 'www'
    tofindb = 'com'
    tofindc = 'log'
    tofindd = 'sign'
    tofinde = 'register'

    for filename in os.listdir(html_path):
        count_page_valid = 0
        count_file_number = count_file_number+1
        print filename
        try: 
            soup = BeautifulSoup(file(html_path+filename))
            # title analysis
            title = soup.find('title')
            contain_title = False
            if (title != None):
                # print soup.title.string
                if (soup.title.string != None):
                    if target in soup.title.string.lower():
                        target_title_number = target_title_number + 1
                        contain_title = True
            if contain_title == True:
                contain_title_number = contain_title_number + 1

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
            if count_page_valid == 0:
                count_file_number = count_file_number-1
        except: pass
    
    count_externallink = count_externallink/count_file_number
    count_internallink = count_internallink/count_file_number
    count_externaljavascript = count_externaljavascript/count_file_number
    count_cssexternallink = count_cssexternallink/count_file_number
    count_login_number = count_login_number
    target_title_number = target_title_number
    print "Totally: "+str(count_file_number)+" htmls analyzed."
    print "Target: "+target
    print "Average externallink number: "+str(count_externallink)
    print "Average internallink number: "+str(count_internallink)
    print "Average external javascript number: "+str(count_externaljavascript)
    print "Average external css link number: "+str(count_cssexternallink)
    print "Total login number: "+str(count_login_number)
    print "Webpages contain login: "+str(contain_login_number)
    print "Total target title number: "+str(target_title_number)
    print "Webpages contain target title: "+str(contain_title_number)

if __name__ == '__main__':
    if len(sys.argv)!=3:
        print 'please use "python html_analysis_average.py html_path target_name"'
        exit(1)
    else:
        html_path = sys.argv[1]
        target_name = sys.argv[2]
        html_analysis_average(html_path, target_name)
        print "The program has finished!"
    

