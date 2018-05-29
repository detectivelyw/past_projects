import Image
import sys

def make_regalur_image(img, size = (1024, 1024)):
    return img.resize(size).convert('RGB')

def split_image(img, part_size = (128, 128)):
    w, h = img.size
    pw, ph = part_size
    
    assert w % pw == h % ph == 0
    
    return [img.crop((i, j, i+pw, j+ph)).copy() \
                for i in xrange(0, w, pw) \
                for j in xrange(0, h, ph)]

def hist_similar(lh, rh):
    assert len(lh) == len(rh)
    return sum(1 - (0 if l == r else float(abs(l - r))/max(l, r)) for l, r in zip(lh, rh))/len(lh)

def calc_similar(li, ri):
#    return hist_similar(li.histogram(), ri.histogram())
    return sum(hist_similar(l.histogram(), r.histogram()) for l, r in zip(split_image(li), split_image(ri))) / 64.0
            

def calc_similar_by_path(lf, rf):
    li, ri = make_regalur_image(Image.open(lf)), make_regalur_image(Image.open(rf))
    return calc_similar(li, ri)

def make_doc_data(lf, rf):
    li, ri = make_regalur_image(Image.open(lf)), make_regalur_image(Image.open(rf))
    li.save(lf + '_regalur.png')
    ri.save(rf + '_regalur.png')
    fd = open('stat.csv', 'w')
    fd.write('\n'.join(l + ',' + r for l, r in zip(map(str, li.histogram()), map(str, ri.histogram()))))
#    print >>fd, '\n'
#    fd.write(','.join(map(str, ri.histogram())))
    fd.close()
    import ImageDraw
    li = li.convert('RGB')
    draw = ImageDraw.Draw(li)
    for i in xrange(0, 1024, 128):
        draw.line((0, i, 1024, i), fill = '#ff0000')
        draw.line((i, 0, i, 1024), fill = '#ff0000')
    li.save(lf + '_lines.png')
    

if __name__ == '__main__':
    if len(sys.argv)!=3:
        print 'please use "python visual_similarity.py path_image_1 path_image_2"'
        exit(1)
    else:
        image_1 = sys.argv[1]
        image_2 = sys.argv[2]
        
        result = calc_similar_by_path(image_1, image_2)
        print "The similarity between the given two images is: "+str(result)
