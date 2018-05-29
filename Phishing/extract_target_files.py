import shelve
import sys

def print_db(url_database_path, target_name, start_number, end_number):
    filename1 = target_name+"-images.txt"
    filename2 = target_name+"-htmls.txt"
    f1 = open(filename1, 'w')
    f2 = open(filename2, 'w')
        
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
    f1.write("Target: "+target_name+"\n")
    f2.write("Target: "+target_name+"\n")
    for current_number in range(start_number_value, end_number_value+1):
        if (db[str(current_number)]["target"] == target_name):
            f1.write(str(current_number)+".png ")
            f2.write(str(current_number)+".html ")
                         
        current_number = current_number+1

    db.close()
    f1.close()
    f2.close()
    return total
        
if __name__ == '__main__':
    if len(sys.argv)!=5:
        print 'please use "python extract_target_files.py url_database_path target_name start_number end_number"'
        exit(1)
    else:
        url_database_path = sys.argv[1]
        target_name = sys.argv[2]
        start_number = sys.argv[3]
        end_number = sys.argv[4]
 
        total_number = print_db(url_database_path, target_name, start_number, end_number)
        print "The program has finished!"
