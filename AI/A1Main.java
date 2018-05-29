//**************************************************************************************
//*                        Genetic Search Algorithm                                    *
//*                                                                                    *
//*  Author: Yiwen Li                                                                  *
//*  CSE Division, EECS, University of Michigan                                        *
//*  Current Version: 1.0                                                              *
//*                                                                                    *
//*  All copyrights reserved                                                           *
//**************************************************************************************


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Polygon;
import ch.reto_hoehener.japng.Apng;
import ch.reto_hoehener.japng.ApngFactory;
import ch.reto_hoehener.japng.JapngException;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;
import static java.lang.Math.log10;
import java.math.*;

// Provides a source of random numbers for mutations and crossover
class Rand {
    private static Random random = new Random();

    public static int nextInt(int n) {
        return random.nextInt(n);
    }

    public static float nextFloat() {
        return random.nextFloat();
    }

    public static double nextDouble() {
        return random.nextDouble();
    }
}

// Represents the x,y coordinates that determine a shape
class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

// Represents the geometry of a polygon
class Poly {
    private Polygon polygon;
    private List<Point> points;

    public Poly(int width, int height, int numberOfsides) {
        points = new ArrayList<Point>();
        // This code creates a polygon with points chosen uniformly randomly
        // from the area of the original image.
        // You can change this to produce a different distribution of polygons
        // Add numberOfsides to allow the generation of multiple sides shapes (3,4,5,6 sides shapes are allowed)
        for (int i = 0; i < numberOfsides; i++)
            points.add(new Point(Rand.nextInt(width), Rand.nextInt(height)));
        polygon = null;
    }

    public Poly(List<Point> points) {
        this.points = points;
        polygon = null;
    }

    public Polygon getPolygon() {
        if (polygon != null)
            return polygon;
        polygon = new Polygon();
        for (Point point: points)
            polygon.addPoint(point.getX(), point.getY());
        return polygon;
    }

    public Polygon getPolygon(int scale) {
        Polygon polygon = new Polygon();
        for (Point point: points)
            polygon.addPoint(scale * point.getX(), scale * point.getY());
        return polygon;
    }
}

// This is the interface for various shapes
interface Shape {
    public void draw(Graphics g, int scale);
    // Add shape mutation type
    public Shape mutate_color();
    public Shape mutate_shape(int width, int height, int numberOfsides);
}

// Represents a polygon with a specific geometry and color
class PolyShape implements Shape {
    private Color color;
    private Poly poly;

    // Creates a new random polygon
    // Can generate multiple sides shapes
    public PolyShape(int width, int height, int numberOfsides) {
        color = new Color(Rand.nextFloat(), Rand.nextFloat(),
                          Rand.nextFloat(), Rand.nextFloat());
        poly = new Poly(width, height, numberOfsides);
    }

    // Creates a new polygon with a specific color and geometry
    public PolyShape(Color color, Poly poly) {
        this.color = color;
        this.poly = poly;
    }

    // Draws the polygon onto a Graphics object
    public void draw(Graphics g, int scale) {
        g.setColor(color);
        g.fillPolygon(poly.getPolygon(scale));
    }

    // Return a new mutated polygon with random color (Same shape)
    public Shape mutate_color() {
    	return new PolyShape(new Color(Rand.nextFloat(), Rand.nextFloat(), Rand.nextFloat(), Rand.nextFloat()),
                             poly);
    }
    
    // Return a new mutated polygon with random shape (Same color)
    public Shape mutate_shape(int width, int height, int numberOfsides) {
    	return new PolyShape(color, new Poly(width, height, numberOfsides));
    }
}

// This is the interface to a factory that creates shapes
interface ShapeFactory {
    public Shape newShape(int width, int height, int numberOfsides);
}

// Factory class for Poly shapes
class PolyFactory implements ShapeFactory {
    public Shape newShape(int width, int height, int numberOfsides) {
        return new PolyShape(width, height, numberOfsides);
    }
}

// Represents an approximation of the original image using shapes
class ImageApprox implements Comparable {
    private static int p = 100;
    private BufferedImage original;
    private ShapeFactory factory;
    private int width, height;
    private double cachedFitness;

    // This is the genome -- an ordered list of shapes.
    private List<Shape> shapes;

    // Makes a random image approximation
    public ImageApprox(BufferedImage original, ShapeFactory factory) {
        this.original = original;
        this.factory = factory;
        width = original.getWidth();
        height = original.getHeight();
        cachedFitness = Double.NaN;
        shapes = new ArrayList<Shape>();
        for (int i = 0; i < p; i++)
            shapes.add(factory.newShape(width, height, 3));
    }
    // Create an image with an existing one
    public ImageApprox(ImageApprox existingImage) {
        this.original = existingImage.original;
    	this.factory = existingImage.factory;
    	width = original.getWidth();
        height = original.getHeight();
        this.cachedFitness = existingImage.cachedFitness;
        shapes = new ArrayList<Shape>();
        for (int i = 0; i < p; i++)
            shapes.add(existingImage.shapes.get(i));
    }
    // The cross-over of two parents to generate a child
    public ImageApprox(ImageApprox parent1, ImageApprox parent2, int C) {
        this.original = parent1.original;
    	this.factory = parent1.factory;
    	width = original.getWidth();
        height = original.getHeight();
        this.cachedFitness = Double.NaN;
        shapes = new ArrayList<Shape>();
        for (int i = 0; i < C; i++)
            shapes.add(parent1.shapes.get(i));
        for (int i = C; i < p; i++)
            shapes.add(parent2.shapes.get(i));
    }
    // Copy an image
    public void CopyImage(ImageApprox existingImage) {
    	this.original = existingImage.original;
    	this.factory = existingImage.factory;
    	width = original.getWidth();
        height = original.getHeight();
        this.cachedFitness = existingImage.cachedFitness;
        for (int i = 0; i < p; i++)
            shapes.set(i, existingImage.shapes.get(i));
    }
    // Returns the rendered image approximation
    public BufferedImage image() {
        return image(1);
    }

    // Returns the rendered image approximation enlarged by scale
    public BufferedImage image(int scale) {
        BufferedImage i = new BufferedImage(scale * width, scale * height,
                                            BufferedImage.TYPE_INT_RGB);
        Graphics g = i.getGraphics();
        for (Shape shape: shapes)
            shape.draw(g, scale);
        return i;
    }

    // Returns the fitness of the individual
    public double fitness() {
        // This code finds the red, green, and blue components of each pixel of
        // the original image and the approximated image, but always returns a
        // fitness of 0
        // Change this to implement the fitness calculation specified in the
        // assignment
        if (!Double.isNaN(cachedFitness))
            return cachedFitness;

        BufferedImage approx = image();
        int[] origPixels = original.getData().
                             getPixels(0, 0, original.getWidth(),
                                       original.getHeight(), (int[]) null);
        int[] apprPixels = approx.getData().
                             getPixels(0, 0, original.getWidth(),
                                       original.getHeight(), (int[]) null);
        // The Computation of fitness specified in the assignment
        int s = 0, t = 0, distance = 0;
        
        for (int i = 0; i < origPixels.length; i += 3) {
            int rt = origPixels[i];
            int gt = origPixels[i+1];
            int bt = origPixels[i+2];
            int rs = apprPixels[i];
            int gs = apprPixels[i+1];
            int bs = apprPixels[i+2];
            s += Math.pow(rs,2) + Math.pow(gs,2) + Math.pow(bs,2); 
            t += Math.pow(rt,2) + Math.pow(gt,2) + Math.pow(bt,2);
            distance += Math.pow(rs-rt,2) + Math.pow(gs-gt,2) + Math.pow(bs-bt,2);
        }
 
        double temp1, temp2;
        temp1 = sqrt(distance);
        temp2 = sqrt(s) + sqrt(t);
 
        cachedFitness = log10(temp2/temp1);
        
        return cachedFitness;
    }

    // The compareTo methods are used for sorting the individuals by fitness
    @Override
    public int compareTo(Object that) {
        return compareTo((ImageApprox) that);
    }

    public int compareTo(ImageApprox that) {
        return (int) signum(that.fitness() - this.fitness());
    }

    // Mutates the image approximation
    public void mutate() {
        // This code mutates a random shape
    	// Mutate with certain probability
    	double mutation_high_probability = 0.05;
    	double mutation_low_probability = 0.01;
    	double random = 0.0;

    	// Mutation 1: Change the color of a single shape: with relatively high probability=0.05
    	random = Rand.nextDouble();
    	if (random <= mutation_high_probability)
    	{
    		int n = Rand.nextInt(shapes.size());
            shapes.set(n, shapes.get(n).mutate_color()); 	    	
    	}
    	
    	// Mutation 2: Change the shape of a single shape: with relatively high probability=0.05
    	random = Rand.nextDouble();
    	if (random <= mutation_high_probability)
    	{
    		int n = Rand.nextInt(shapes.size());
            shapes.set(n, shapes.get(n).mutate_shape(width, height, 3)); 	    	
    	}
    	
    	// Mutation 3: Mutate the shape to a 4,5,or 6 sides shape: with relatively low probability=0.01
    	random = Rand.nextDouble();
    	if (random <= mutation_low_probability)
    	{
    		int n = Rand.nextInt(shapes.size());
    		int numberOfsides, random_index;
    		int sides[] = new int[3];
    		sides[0] = 4; sides[1] = 5; sides[2] = 6;
    		random_index = Rand.nextInt(3);
    		numberOfsides = sides[random_index];
            shapes.set(n, shapes.get(n).mutate_shape(width, height, numberOfsides)); 	    	
    	}

        // Mutation 4: Exchange two shapes: with relatively high probability=0.05
    	random = Rand.nextDouble();
    	if (random <= mutation_high_probability)
    	{
    		 int n1 = Rand.nextInt(shapes.size());
    	     int n2 = Rand.nextInt(shapes.size());
    	     if (n1 != n2)
    	     {
    	    	 Shape tempshape;
    	         tempshape = shapes.get(n1);
    	         shapes.set(n1, shapes.get(n2));
    	         shapes.set(n2, tempshape);
    	     }	    	
    	}
        
        cachedFitness = Double.NaN;
    }
}

class GeneticSearch {
    private BufferedImage original;
    private int n, k;
    private ImageApprox[] population;

    // Creates a population of n random individuals
    public GeneticSearch(BufferedImage original, int n, int k) {
        this.original = original;
        this.n = n; // population size
        this.k = k; // number of children per generation
        population = new ImageApprox[n];
        for (int i = 0; i < n; i++)
            population[i] = new ImageApprox(original, new PolyFactory());
        Arrays.sort(population);    
    }

    // Makes a new generation of individuals and returns the effort to do so
    // The effort is equal to the number of children in the generation
    public int step() {
        // This code leaves the population unchanged
        // Change this to create children by selecting parents and using
        // crossover to combine their genomes.
    	// Russian Roulette Algorithm is used for parents selection
    	// This function includes cross-over and mutation to generate a new generation
    	
    	int i, j;
    	int C; // the random number which decides the cross-over point
    	int parent1 = 0, parent2 = 0; 
    	double fitness_sum = 0.0;
    	double rassian_roulette_randomnumber, temp_probability_sum;
    	ImageApprox[] new_generation;
    	new_generation = new ImageApprox[n+k];
    	
    	for (i = 0; i < n; i++)
    	{
    		fitness_sum += population[i].fitness();
    		new_generation[i] = new ImageApprox(population[i]);
    	}
    	// The progress for the birth of each child
    	for (i = 0; i < k; i++)
    	{
    		// Select parent1
    		temp_probability_sum = 0.0;
    		for (j = 0; j < n; j++)
    		{
    			rassian_roulette_randomnumber = Rand.nextDouble();
    			temp_probability_sum += population[j].fitness()/fitness_sum;
    			if (rassian_roulette_randomnumber <= temp_probability_sum)
    			{
    				parent1 = j;
    				break;
    			}
    		}
    		
    		// Select parent2
    		temp_probability_sum = 0.0;
    		for (j = 0; j < n; j++)
    		{
    			rassian_roulette_randomnumber = Rand.nextDouble();
    			temp_probability_sum += population[j].fitness()/fitness_sum;
    			if (rassian_roulette_randomnumber <= temp_probability_sum)
    			{
    				parent2 = j;
    				break;
    			}
    		}
    		
    		// Give birth to a child by cross-over
    		C = Rand.nextInt(100); // choosing the cross-over point randomly from 0 to 99
    		new_generation[n+i] = new ImageApprox(population[parent1], population[parent2], C);
    		
    		// Mutate the children with probability
        	new_generation[n+i].mutate();      	
    	}

    	// Choose the best N individuals as the new population  
    	Arrays.sort(new_generation);
    	for (j = 0; j < n; j++)
    	{
    		population[j].CopyImage(new_generation[j]); 
    	}
        return k;
    }

    // Returns the original image
    public BufferedImage getOriginal() {
        return original;
    }

    // Returns the best current image approximation, assuming the individuals
    // are sorted in decreasing order of fitness.
    public ImageApprox getBest() {
        return population[0];
    }
}

// Displays the original image and the best current approximation
class Visualization extends Frame {
    public static final long serialVersionUID = 2011091201L;
    private static int TopBorder = 32;
    private GeneticSearch s;
    private int scale;

    public Visualization(GeneticSearch s) {
        super("Visualization");
        this.s = s;
        scale = 1;
        int w = s.getOriginal().getWidth();
        int h = s.getOriginal().getHeight();
        while ((scale + 1) * w <= 600 && (scale + 1) * h <= 800)
            scale++;
        setSize(2 * scale * w, scale * h + TopBorder);
        setVisible(true);
    }

    public void paint(Graphics g) {
        Dimension dim = getSize();
        int w = s.getOriginal().getWidth();
        int h = s.getOriginal().getHeight();
        g.drawImage(s.getOriginal().
                     getScaledInstance(scale * w, scale * h, Image.SCALE_FAST),
                    0, TopBorder, this);
        g.drawImage(s.getBest().image(scale), scale * w, TopBorder, this);
    }
    
    public void update(Graphics g) {
        paint(g);
    }
}

public class A1Main {
    public static void main(String[] args) {
        final int reportFreq = 1000;
        int n = 0, k = 0, e = 0;
        String filename = null;
        try {
            n = Integer.parseInt(args[0]);
            k = Integer.parseInt(args[1]);
            e = Integer.parseInt(args[2]);
            filename = args[3];
        } catch (Exception ex) {
            System.out.println("Usage: java A1Main <n> <k> <e> <image>");
            System.exit(0);
        }
        try {
            BufferedImage image = ImageIO.read(new File(filename));
            GeneticSearch s = new GeneticSearch(image, n, k);
            Visualization v = new Visualization(s);
            Apng apng = ApngFactory.createApng();
            apng.setPlayCount(1);
            int effort = n;
            int effortReport = reportFreq;
            while (effort <= e) {
                v.repaint();
                if (effort >= effortReport) {
                    apng.addFrame(s.getBest().image(), 1000 / 15);
                    effortReport += reportFreq;
                }
                // This is for test purpose only
                /*
                if ((effort==2)||(effort==100)||(effort==250)||(effort==500)||(effort==1000)||(effort==2000)||(effort==4000)||(effort==6000)||
                		(effort==8000)||(effort==10000)||(effort==15000)||(effort==20000)||(effort==25000)||(effort==30000)||(effort==40000)||
                		(effort==50000)||(effort==60000)||(effort==70000)||(effort==80000)||(effort==90000)||(effort==100000)||
                		(effort==120000)||(effort==150000)||(effort==180000)||(effort==200000))
                {
                	System.out.println(effort + "  " + s.getBest().fitness());
                }
                */
                System.out.println(effort + "  " + s.getBest().fitness());
                effort += s.step();
            }
            apng.assemble(new File(filename + ".png"));
        } catch (IOException ioe) {
            System.out.println("IO Error");
        } catch (JapngException je) {
            System.out.println("Japng Error");
        }
        System.exit(0);
    }
}
