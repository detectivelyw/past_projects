import shelve
import sys

def print_db(url_database_path, f, start_number, end_number):
    target1 = "PayPal"
    target2 = "Sulake Corporation"
    target3 = "Orkut"
    target4 = "Tibia"
    target5 = "Facebook"
    target1_number = 0
    target2_number = 0
    target3_number = 0
    target4_number = 0
    target5_number = 0
    
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
        #print str(current_number)+": ", db[str(current_number)]["target"]
        #f.write(str(current_number)+": "+db[str(current_number)]["target"]+"\n")
        if db[str(current_number)]["target"] == target1:
            target1_number = target1_number+1
        if db[str(current_number)]["target"] == target2:
            target2_number = target2_number+1
        if db[str(current_number)]["target"] == target3:
            target3_number = target3_number+1
        if db[str(current_number)]["target"] == target4:
            target4_number = target4_number+1
        if db[str(current_number)]["target"] == target5:
            target5_number = target5_number+1
            
        current_number = current_number+1

    f.write(target1+" : "+str(target1_number)+"\n")
    f.write(target2+" : "+str(target2_number)+"\n")
    f.write(target3+" : "+str(target3_number)+"\n")
    f.write(target4+" : "+str(target4_number)+"\n")
    f.write(target5+" : "+str(target5_number)+"\n")
    
    db.close()
    return total
        
if __name__ == '__main__':
    if len(sys.argv)!=5:
        print 'please use "python most_popular_target.py url_database_path filename start_number end_number"'
        exit(1)
    else:
        url_database_path = sys.argv[1]
        filename = sys.argv[2]
        start_number = sys.argv[3]
        end_number = sys.argv[4]
        f = open(filename, 'w')
        total_number = print_db(url_database_path, f, start_number, end_number)
        f.close()
        print "The program has finished!"
        print "Totally "+str(total_number)+" urls have been processed."
        print "The result has been saved to the file "+filename+"."