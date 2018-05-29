import os
import sys
import visual_similarity

def compute_visual_similarity(phishing_images_path, target_image_path, output_file_path):
    f = open(output_file_path, 'w')
    print "Visual similarity score: "
    average_score = 0
    total_number = 0
    for filename in os.listdir(phishing_images_path):
        try:
            phishing_image_path = phishing_images_path + filename
            similarity_score = visual_similarity.calc_similar_by_path(phishing_image_path, target_image_path)
            average_score = average_score + similarity_score
            total_number = total_number + 1
            print str(total_number)+": "+phishing_image_path+" "+target_image_path+" : "+str(similarity_score)
            f.write(str(similarity_score)+", ")
        except: pass
    average_score = average_score/total_number
    print "Total: "+str(total_number)+" scores computed."
    print "Average score: "+str(average_score)
    f.write("\n")
    f.write("Total: "+str(total_number)+" scores computed.\n")
    f.write("Average score: "+str(average_score))
    f.close()
        
if __name__ == '__main__':
    if len(sys.argv)!=4:
        print 'please use "python target_visual_similarity.py phishing_images_path target_image_path output_file_path"'
        exit(1)
    else:
        phishing_images_path = sys.argv[1]
        target_image_path = sys.argv[2]
        output_file_path = sys.argv[3]
        compute_visual_similarity(phishing_images_path, target_image_path, output_file_path)
        print "The program has finished!"
    

