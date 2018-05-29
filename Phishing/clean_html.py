# delete invalid HTML files in a given folder
# BeautifulSoup must be install before use

from BeautifulSoup import BeautifulSoup
import os
import sys

def clean_html(html_path):
    output_filename = "clean_html.txt"
    f = open(output_filename, 'w')
    
    check_title1 = "403"
    check_title2 = "404"
    check_title3 = "disabled"
    check_title4 = "account suspended"
    check_title5 = "unavailable"
    check_title6 = "error"
    
    for filename in os.listdir(html_path):
        try:
            print filename
            page = open(html_path+filename)
            line = page.readline()
            page.close()
            if line == "Unreachable":
                f.write(filename+" ")
                continue
            contain = line.lower.find(check_title1)
            if contain != -1:
                f.write(filename+" ")
                continue
            contain = line.lower.find(check_title2)
            if contain != -1:
                f.write(filename+" ")
                continue
            contain = line.lower.find(check_title3)
            if contain != -1:
                f.write(filename+" ")
                continue
            contain = line.lower.find(check_title4)
            if contain != -1:
                f.write(filename+" ")
                continue
            contain = line.lower.find(check_title5)
            if contain != -1:
                f.write(filename+" ")
                continue
            contain = line.lower.find(check_title6)
            if contain != -1:
                f.write(filename+" ")
                continue

            else: 
                soup = BeautifulSoup(file(html_path+filename))
                # title analysis
                title = soup.find('title')
                if (title != None):
                    # print soup.title.string
                    if (soup.title.string != None):
                        if check_title1 in soup.title.string.lower():
                            f.write(filename+" ")
                        if check_title2 in soup.title.string.lower():
                            f.write(filename+" ")
                        if check_title3 in soup.title.string.lower():
                            f.write(filename+" ")
                        if check_title4 in soup.title.string.lower():
                            f.write(filename+" ")
                        if check_title5 in soup.title.string.lower():
                            f.write(filename+" ")
                        if check_title6 in soup.title.string.lower():
                            f.write(filename+" ")
        except: pass
    f.close()

if __name__ == '__main__':
    if len(sys.argv)!=2:
        print 'please use "python clean_html.py html_path"'
        exit(1)
    else:
        html_path = sys.argv[1]
        clean_html(html_path)
        print "The program has finished!"
    

