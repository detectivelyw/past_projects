import shelve
import sys
        
def url_analysis_db(url_database_path, target, start_number, end_number):
    filename = target+"-url-length.txt"
    f = open(filename, 'w')
    count_contain_string = 0
    count_url_length = 0
    count_url_number = 0
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
    for current_number in range(start_number_value, end_number_value+1):
        contain = -1;
        if target == "All":
            url = db[str(current_number)]["url"]
            count_url_number = count_url_number+1
            print url
            count_url_length = count_url_length + len(url)
            f.write(str(len(url))+", ")
        else:
            if db[str(current_number)]["target"] == target:
                url = db[str(current_number)]["url"]
                count_url_number = count_url_number+1
                print url
                count_url_length = count_url_length + len(url)
                f.write(str(len(url))+", ")
                url0 = url.lower()
                contain = url0.find(target.lower())
                if contain != -1:
                    count_contain_string = count_contain_string+1

        current_number = current_number+1
    
    count_url_length = count_url_length/count_url_number
    print "Totally: "+str(count_url_number)+" urls analyzed."
    print "Average url length: "+str(count_url_length)
    print "URLs containing character string number: "+str(count_contain_string)
    db.close()
    f.close()
    return total

if __name__ == '__main__':
    if len(sys.argv)!=5:
        print 'please use "python url_analysis_db.py url_database_path target start_number end_number"'
        exit(1)
    else:
        url_database_path = sys.argv[1]
        target = sys.argv[2]
        start_number = sys.argv[3]
        end_number = sys.argv[4]
        total_number = url_analysis_db(url_database_path, target, start_number, end_number)
        print "The program has finished!"
