//**************************************************************************************
//*                    Real-time Indoor Scene Understanding                            *
//*                  Using Bayesian Filtering with Motion Cues                         *
//*                                                                                    *
//*  Author: Yiwen Li                                                                  *
//*  CSE Division, EECS, University of Michigan                                        *
//*  Current Version: 1.0                                                              *
//*                                                                                    *
//*  All copyrights reserved                                                           *
//**************************************************************************************

import java.io.*;

class Hypothesis {
	private Point_Feature p1;
	private Point_Feature p2;
	private Point_Feature p3;
	private Point_Feature p4;
	private Wall_Plane wall_left;
	private Wall_Plane wall_center;
	private Wall_Plane wall_right;
	public Hypothesis(double u1, double v1, double u2, double v2, double u3, double v3, double u4, double v4) {
		p1 = new Point_Feature(u1,v1);
		p2 = new Point_Feature(u2,v2);
		p3 = new Point_Feature(u3,v3);
		p4 = new Point_Feature(u4,v4);
		wall_left = new Wall_Plane(this.p1,  this.p2);
		wall_center = new Wall_Plane(this.p2,  this.p3);
		wall_right = new Wall_Plane(this.p3,  this.p4);
	}
	
	public String where_point(Point_Feature point) {
		String result;
		double k1 = (this.p1.get_v()-this.p2.get_v())/(this.p1.get_u()-this.p2.get_u());
		double b1 = this.p1.get_v() - k1* this.p1.get_u();
		double k2 = (this.p2.get_v()-this.p3.get_v())/(this.p2.get_u()-this.p3.get_u());
		double b2 = this.p2.get_v() - k1* this.p2.get_u();
		double k3 = (this.p3.get_v()-this.p4.get_v())/(this.p3.get_u()-this.p4.get_u());
		double b3 = this.p3.get_v() - k1* this.p3.get_u();
		
		if ((point.get_u() < this.p2.get_u())&&(point.get_v() < k1*point.get_u()+b1)) {
			result = "left";
			return result;
		}
		
		if ((point.get_u() > this.p3.get_u())&&(point.get_v() < k3*point.get_u()+b3)) {
			result = "right";
			return result;
		}
		
		if ((point.get_u() >= this.p2.get_u())&&(point.get_u() <= this.p3.get_u())&&(point.get_v() < k2*point.get_u()+b2)) {
			result = "center";
			return result;
		}
		
		result = "ground";
		return result;
	}
	
	public Wall_Plane get_wall_left() {
		return this.wall_left;
	}
	
	public Wall_Plane get_wall_center() {
		return this.wall_center;
	}
	
	public Wall_Plane get_wall_right() {
		return this.wall_right;
	}
	
	// This is for debug purpose 
	public void print() {
		System.out.print(this.p1.get_u()+" "+this.p1.get_v()+" "+this.p2.get_u()+" "+this.p2.get_v()+" "+
				this.p3.get_u()+" "+this.p3.get_v()+" "+this.p4.get_u()+" "+this.p4.get_v()+"\n");
	}
}

class Motion {
	private double theta;
	private double x;
	private double z;
	public Motion(double theta_given, double x_given, double z_given) {
		this.theta = theta_given;
		this.x = x_given;
		this.z = z_given;
	}
	
	public double get_theta() {
		return this.theta;
	}
	
	public double get_x() {
		return this.x;
	}
	
	public double get_z() {
		return this.z;
	}
	
	// This is for debug purpose
	public void print() {
		System.out.print(this.theta+" "+this.x+" "+this.z+"\n");
	}
}

class Vector_3D {
	private double x;
	private double y;
	private double z;
	public Vector_3D(double x_given, double y_given, double z_given) {
		this.x = x_given;
		this.y = y_given;
		this.z = z_given;
	}
	
	public double get_x() {
		return this.x;
	}
	
	public double get_y() {
		return this.y;
	}
	
	public double get_z() {
		return this.z;
	}
}

class Wall_Plane {
	private Point_Feature p1;
	private Point_Feature p2;
	private Vector_3D vb;
	private Vector_3D nw;
	private double dw;
	public Wall_Plane(Point_Feature p1_given,  Point_Feature p2_given) {
		this.p1 = p1_given;
		this.p2 = p2_given;
		this.p1.transform_to_3D_location_ground();
		this.p2.transform_to_3D_location_ground();
		this.vb = new Vector_3D(this.p1.get_x()-this.p2.get_x(), this.p1.get_y()-this.p2.get_y(), this.p1.get_z()-this.p2.get_z());
		this.nw = new Vector_3D(this.vb.get_z()*(-1.0), 0.0, this.vb.get_x());
		this.dw = this.nw.get_x()*this.p1.get_x() + this.nw.get_y()*this.p1.get_y() + this.nw.get_z()*this.p1.get_z();
	}
	
	public Vector_3D get_nw() {
		return this.nw;
	}
	
	public double get_dw() {
		return this.dw;
	}
}

class Point_Feature {
	private double u; // original 2D location
	private double v; // original 2D location
	private double u_predict; // the predicted 2D location (using motion cue)
	private double v_predict; // the predicted 2D location (using motion cue)
	private double u_n; // transformed location in normalized camera space
	private double v_n; // transformed location in normalized camera space
	private double x; // 3D location
	private double y; // 3D location
	private double z; // 3D location
	
	public Point_Feature(double u_given, double v_given) {
		this.u = u_given;
		this.v = v_given;
		this.transform_to_normalized_camera_space();
	}
	
	public void transform_to_normalized_camera_space() {
		double f_u = 1389.182714;
		double f_v = 1394.598277;
		double u_0 = 672.605430;
		double v_0 = 387.235803;
		this.u_n = (u-u_0)/f_u;
		this.v_n = (v-v_0)/f_v;
	}
	
	public void transform_to_3D_location_ground() {
		this.x = this.u_n/this.v_n;
		this.y = 1;
		this.z = 1/this.v_n;
	}
	
	public void transform_to_3D_location_wall(Wall_Plane wall) {
		double z;
		double temp;
		temp = wall.get_nw().get_x()*this.u_n + wall.get_nw().get_y()*this.v_n + wall.get_nw().get_z();
		z = wall.get_dw()/temp;
		this.x = z*this.u_n;
		this.y = z*this.v_n;
		this.z = z;
	}
	
	public void predict_2D_location (Motion motion) {
		double f_u = 1389.182714;
		double f_v = 1394.598277;
		double u_0 = 672.605430;
		double v_0 = 387.235803;
		Vector_3D Qt = new Vector_3D(Math.cos(motion.get_theta())*this.x+Math.sin(motion.get_theta())*this.z+motion.get_x(), 
				this.y, (-1.0)*Math.sin(motion.get_theta())*this.x+Math.cos(motion.get_theta())*this.z+motion.get_z());
		this.u_predict = f_u*Qt.get_x()/Qt.get_z() + u_0;
		this.v_predict = f_v*Qt.get_y()/Qt.get_z() + v_0;
	}
	
	public double get_u() {
		return this.u;
	}
	
	public double get_v() {
		return this.v;
	}
	
	public double get_u_predict() {
		return this.u_predict;
	}
	
	public double get_v_predict() {
		return this.v_predict;
	}
	
	public double get_x() {
		return this.x;
	}
	
	public double get_y() {
		return this.y;
	}
	
	public double get_z() {
		return this.z;
	}
	
	// This is for debug purpose
	public void print() {
		System.out.print(this.u+" "+this.v+"\n");
	}
}

class Video_Analysis {
	private String video_name;
	private String features_path;
	private String hypotheses_path;
	private String motion_path;
	private int number_of_features;
	private int number_of_hypotheses;
	private int max_number_of_features;
	private Point_Feature[][] features;
	private Point_Feature[][] features_frame0;
	private Hypothesis[] hypotheses;
	private Motion[] motion;
	private double [][] probabilities;
	private double sigma;
	private double epsilon_limit;
	
	public Video_Analysis(String name, double sigma_given, double epsilon) {
		this.video_name = name;
		this.features_path = name+"/data/features.txt";
		this.hypotheses_path = name+"/data/hypotheses.txt";
		this.motion_path = name+"/data/motion.txt";
		this.sigma = sigma_given;
		this.epsilon_limit = epsilon;
		try {
			int i;
			this.max_number_of_features = 0;
			if (this.video_name.equals("Basement")) {
				this.max_number_of_features = 281;
			}
			if (this.video_name.equals("EECS_Building")) {
				this.max_number_of_features = 257;
			}
			if (this.video_name.equals("Non_Parallel1")) {
				this.max_number_of_features = 518;
			}
			
			this.features = new Point_Feature[301][this.max_number_of_features+1];
			FileReader fr1 = new FileReader(this.features_path);
			BufferedReader br1 = new BufferedReader(fr1);
			String myreadline1;
			for (i = 0; i <= 300; i++) {
				int j;
				int last_index = 0;
				myreadline1 = br1.readLine();
				String[] data0 = myreadline1.split(" ");
				this.number_of_features = Integer.parseInt(data0[1]);
				for (j = 0; j < this.number_of_features; j++) {
					myreadline1 = br1.readLine();
					String[] data = myreadline1.split(" ");
					int current_index = Integer.parseInt(data[0]);
					int k;
					for (k = last_index; k < current_index; k++) {
						this.features[i][k] = new Point_Feature(-1.0, -1.0);
					}
					last_index = current_index+1;
					this.features[i][current_index] = new Point_Feature(Double.parseDouble(data[1]),Double.parseDouble(data[2]));
				}
			}	
			br1.close();
			fr1.close();
			/*
			System.out.print("0 \n");
			for (i = 0; i <= max_number_of_features; i++) {
				System.out.print(i+" ");
				this.features[0][i].print();
			}
			System.out.print("300 \n");
			for (i = 0; i <= max_number_of_features; i++) {
				System.out.print(i+" ");
				this.features[300][i].print();
			}
			*/
			
			FileReader fr2 = new FileReader(this.hypotheses_path);
			BufferedReader br2 = new BufferedReader(fr2);
			String myreadline2;
			myreadline2 = br2.readLine();
			this.number_of_hypotheses = Integer.parseInt(myreadline2);
			this.hypotheses = new Hypothesis[this.number_of_hypotheses];
			this.probabilities = new double[301][this.number_of_hypotheses];
			// System.out.print(this.number_of_hypotheses+"\n");
			for (i = 0; i < this.number_of_hypotheses; i++) {
				myreadline2 = br2.readLine();
				String[] data = myreadline2.split(" ");
				hypotheses[i] = new Hypothesis(Double.parseDouble(data[1]),Double.parseDouble(data[2]),Double.parseDouble(data[3]),
						Double.parseDouble(data[4]),Double.parseDouble(data[5]),Double.parseDouble(data[6]),
						Double.parseDouble(data[7]),Double.parseDouble(data[8]));
			}
			br2.close();
			fr2.close();
			/*
			for (i = 0; i < this.number_of_hypotheses; i++) {
				System.out.print(i+" ");
				hypotheses[i].print();
			}
			*/
			
			FileReader fr3 = new FileReader(this.motion_path);
			BufferedReader br3 = new BufferedReader(fr3);
			String myreadline3;
			this.motion = new Motion[301];
			for (i = 0; i <= 300; i++) {
				myreadline3 = br3.readLine();
				String[] data = myreadline3.split(" ");
				motion[i] = new Motion(Double.parseDouble(data[1]),Double.parseDouble(data[2]),Double.parseDouble(data[3]));
			}
			br3.close();
			fr3.close();
			/*
			for (i = 0; i <= 300; i++) {
				System.out.print(i+" ");
				motion[i].print();
			}
			*/
		}
		catch (IOException e) {
            e.printStackTrace();
		}
	}
	
	public void bayesian_filtering() {
		int hypothesis_number, feature_number, frame_number;
		this.features_frame0 = new Point_Feature[this.number_of_hypotheses][this.max_number_of_features+1];
		for (feature_number = 0; feature_number <= this.max_number_of_features; feature_number++) {
			for (hypothesis_number = 0; hypothesis_number < this.number_of_hypotheses; hypothesis_number++) {
				this.features_frame0[hypothesis_number][feature_number] 
						= new Point_Feature(this.features[0][feature_number].get_u(), this.features[0][feature_number].get_v());
			}
		}
		
		for (hypothesis_number = 0; hypothesis_number < this.number_of_hypotheses; hypothesis_number++) {
			for (feature_number = 0; feature_number <= this.max_number_of_features; feature_number++) {
				if ((this.features_frame0[hypothesis_number][feature_number].get_u() >= 0)
						&&(this.features_frame0[hypothesis_number][feature_number].get_v() >= 0)) {
					String where = this.hypotheses[hypothesis_number].where_point(this.features_frame0[hypothesis_number][feature_number]);
					if (where.equals("left")) {
						this.features_frame0[hypothesis_number][feature_number].
						transform_to_3D_location_wall(this.hypotheses[hypothesis_number].get_wall_left());
					}
					if (where.equals("center")) {
						this.features_frame0[hypothesis_number][feature_number].
						transform_to_3D_location_wall(this.hypotheses[hypothesis_number].get_wall_center());
					}
					if (where.equals("right")) {
						this.features_frame0[hypothesis_number][feature_number].
						transform_to_3D_location_wall(this.hypotheses[hypothesis_number].get_wall_right());
					}
					if (where.equals("ground")) {
						this.features_frame0[hypothesis_number][feature_number].transform_to_3D_location_ground();
					}
				}
			}
		}
		
		for (hypothesis_number = 0; hypothesis_number < this.number_of_hypotheses; hypothesis_number++) {
			this.probabilities[0][hypothesis_number] = 1.0/this.number_of_hypotheses;
		}

		for (frame_number = 1; frame_number <= 300; frame_number++) {
			double sum_probability = 0.0;
			for (hypothesis_number = 0; hypothesis_number < this.number_of_hypotheses; hypothesis_number++) {
				this.probabilities[frame_number][hypothesis_number] = this.probabilities[frame_number-1][hypothesis_number];
				double temp = 0.0;
				for (feature_number = 0; feature_number <= this.max_number_of_features; feature_number++) {
					if ((this.features_frame0[hypothesis_number][feature_number].get_u() >= 0)
							&&(this.features_frame0[hypothesis_number][feature_number].get_v() >= 0)
							&&(this.features[frame_number][feature_number].get_u() >= 0)
							&&(this.features[frame_number][feature_number].get_v() >= 0)) {
						this.features_frame0[hypothesis_number][feature_number].predict_2D_location(this.motion[frame_number]);
						
						temp = temp
								-Math.pow((this.features_frame0[hypothesis_number][feature_number].get_u_predict()-this.features[frame_number][feature_number].get_u()), 2.0)
								-Math.pow((this.features_frame0[hypothesis_number][feature_number].get_v_predict()-this.features[frame_number][feature_number].get_v()), 2.0); 	
					}
				}
				temp = temp/2;
				temp = temp/(this.sigma*this.sigma);
				temp = Math.exp(temp);
				this.probabilities[frame_number][hypothesis_number] = this.probabilities[frame_number][hypothesis_number]*temp;
				
				if (this.probabilities[frame_number][hypothesis_number] < this.epsilon_limit) {
					this.probabilities[frame_number][hypothesis_number] = this.epsilon_limit;
				}
				sum_probability = sum_probability + this.probabilities[frame_number][hypothesis_number];
			}
			
			for (hypothesis_number = 0; hypothesis_number < this.number_of_hypotheses; hypothesis_number++) {
				this.probabilities[frame_number][hypothesis_number] = this.probabilities[frame_number][hypothesis_number]/sum_probability;
			}
		}
	}
	
	public void output_result() {
		String output_path = this.video_name+"-"+this.sigma+"-output.txt";
		try {
			FileWriter fw = new FileWriter(output_path);
			BufferedWriter bw = new BufferedWriter(fw);
			String my_write;
			bw.write(my_write); 
			my_write = "Data for Video name: "+this.video_name+", Sigma = "+this.sigma+"\n";
			bw.write(my_write); 
			
			int hypothesis_number, frame_number;			
			for (hypothesis_number = 0; hypothesis_number < this.number_of_hypotheses; hypothesis_number++) {
				my_write = "Hypothesis "+hypothesis_number+": \n";
				bw.write(my_write); 
				for (frame_number = 0; frame_number < 300; frame_number++) {
					my_write = this.probabilities[frame_number][hypothesis_number]+", ";
					bw.write(my_write); 
				}
				my_write = this.probabilities[300][hypothesis_number]+"\n";
				bw.write(my_write); 
			}
			
			bw.flush();
            bw.close();
            fw.close();
		}
		catch (IOException e) {
            e.printStackTrace();
		}
	}
}

public class A3Main {
    public static void main(String[] args) {
        String video_name;
        double sigma;
        try {
            video_name = args[0];
            sigma = Double.parseDouble(args[1]);
            
            double epsilon = 1E-10;
            System.out.print("Program starts running...\n");
            Video_Analysis video = new Video_Analysis(video_name, sigma, epsilon);
            System.out.print("Reading data successful! \n");
            System.out.print("Processing data...\n");
            video.bayesian_filtering();
            System.out.print("Processing data successful! \n");
            System.out.print("Generating output file... \n");
            video.output_result();
            System.out.print("Generating output file successful! \n");
            System.out.print("Program has finished successful! \n");
            
        } catch (Exception ex) {
            System.out.println("Usage: java A3Main <Video Name> <Sigma>");
            System.out.println("The <Video Name> must be Basement or EECS_Building or Non_Parallel1");
            System.exit(0);
        }
    }
}