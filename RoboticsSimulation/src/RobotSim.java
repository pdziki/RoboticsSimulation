import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
/*
 * Throughout the program, the unit of 15 is equivalent to 1 foot.
 */
@SuppressWarnings("serial")
public class RobotSim extends JPanel{
	

	public Robot robot = new Robot(50,50);
	public static JPanel inputPanel;
	public static JFrame mainFrame;
	
	static int xPoly[] = new int[4];
	static int yPoly[] = new int[4];
	
	public static double speed;
	public static double direction;
	public static double rateOfRotation;
	public static double wheelDiameter = 1;
	
	public static double rotTopRight;
	public static double rotTopLeft;
	public static double rotBottomRight;
	public static double rotBottomLeft;
	
	static double oneThird = (double) 1/3;
	static double negativeOneThird = (double) -1/3;
	public static double lengthMatrix[][]= {{1,1,1,1},{1,-1,-1,1},{negativeOneThird,oneThird,negativeOneThird,oneThird}}; 
	public static double rotationMatrix[]= {rotTopLeft,rotTopRight,rotBottomLeft,rotBottomRight}; 

	public static double radiusDiv4 = .25;
	public static double[] outputMatrix = {0,0,0};
	
	public static double Vx = 0;
	public static double Vz = 0;
	public static double omega = 0;
	public static boolean resetNeeded = false;
	
	public static double wayPointX = 0;
	public static double wayPointY = 0;
	
	static JLabel wayYLabel = new JLabel("Waypoint Y Coord: " + wayPointY);
	static JLabel wayXLabel = new JLabel("Waypoint X Coord: " + wayPointX);
	
	static boolean resetTimer = false;
	
	static boolean wayPointSelected = false;
	
	public static void main(String[] args) {
				
		JFrame inputFrame = new JFrame();
		inputFrame.setLayout(new BorderLayout());
		inputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inputFrame.setPreferredSize(new Dimension(200,600));
		createInputPanel();
		inputFrame.add(inputPanel);
		inputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inputFrame.pack();
		inputFrame.setVisible(true);
		new RobotSim();
		
	}
		
	public RobotSim() {

		JFrame frame = new JFrame("RobotSim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TestPane());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
		
	}
	
	public static JPanel createInputPanel() {

		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
		inputPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		JLabel speedLabel = new JLabel("Speed");
		inputPanel.add(speedLabel);
		JTextField speedInput = new JTextField(5);
		inputPanel.add(speedInput);
		JLabel directionLabel = new JLabel("Direction");
		inputPanel.add(directionLabel);
		JTextField directionInput = new JTextField(5);
		inputPanel.add(directionInput);
		JLabel rotationLabel = new JLabel("Rate of Rotation");
		inputPanel.add(rotationLabel);
		JTextField rateOfRotationInput = new JTextField(5);
		inputPanel.add(rateOfRotationInput);
		JButton autonomous = new JButton("Autonomous");
				
		JLabel rotTopLeftLabel = new JLabel("RotRateTopLeft");
		inputPanel.add(rotTopLeftLabel);
		JTextField rotTopLeftInput = new JTextField(5);
		inputPanel.add(rotTopLeftInput);
		JLabel rotTopRightLabel = new JLabel("RotRateTopRight");
		inputPanel.add(rotTopRightLabel);
		JTextField rotTopRightInput = new JTextField(5);
		inputPanel.add(rotTopRightInput);
		JLabel rotBottomLeftLabel = new JLabel("RotRateBottomLeft");
		inputPanel.add(rotBottomLeftLabel);
		JTextField rotBottomLeftInput = new JTextField(5);
		inputPanel.add(rotBottomLeftInput);
		JLabel rotBottomRightLabel = new JLabel("RotRateBottomRight");
		inputPanel.add(rotBottomRightLabel);
		JTextField rotBottomRightInput = new JTextField(5);
		inputPanel.add(rotBottomRightInput);

		inputPanel.add(autonomous);
		JButton stop = new JButton("Stop");
		inputPanel.add(stop);
		JButton reset = new JButton("Reset");
		inputPanel.add(reset);
		
		JLabel vXLabel = new JLabel("Vx: " + Vx + " ft/s");	
		JLabel vZLabel = new JLabel("Vz: " + Vz + " ft/s");
		JLabel omegaLabel = new JLabel("Omega: " + omega);

		JLabel timeLabel = new JLabel();
		timeLabel.setText("Time: " + 0 + " seconds");
		wayXLabel.setVisible(false);
        wayYLabel.setVisible(false);

		inputPanel.add(vXLabel);
		inputPanel.add(vZLabel);
		inputPanel.add(omegaLabel);
		inputPanel.add(timeLabel);
		inputPanel.add(wayXLabel);
		inputPanel.add(wayYLabel);
		
		autonomous.addActionListener(new ActionListener(){  
		    public void actionPerformed(ActionEvent e){  
		    	if(!speedInput.getText().isEmpty()) {
		    		speed = Double.parseDouble(speedInput.getText());
		    	}
		    	if(!directionInput.getText().isEmpty()) {
		    		direction = Double.parseDouble(directionInput.getText());
		    	}
		    	if(!rateOfRotationInput.getText().isEmpty()) {
		    		rateOfRotation = Double.parseDouble(rateOfRotationInput.getText());
		    	}
		    	if(!rotTopRightInput.getText().isEmpty()) {
		    		rotTopLeft = Double.parseDouble(rotTopLeftInput.getText());
		    		rotationMatrix[0] = rotTopLeft;
		    	}
		    	if(!rotTopLeftInput.getText().isEmpty()) {
		    		rotTopRight = Double.parseDouble(rotTopRightInput.getText());
		    		rotationMatrix[1] = rotTopRight;
		    	}
		    	
		    	if(!rotBottomRightInput.getText().isEmpty()) {
		    		rotBottomLeft = Double.parseDouble(rotBottomLeftInput.getText());
		    		rotationMatrix[2] = rotBottomLeft;
		    	}
		    	
		    	if(!rotBottomLeftInput.getText().isEmpty()) {
		    		rotBottomRight = Double.parseDouble(rotBottomRightInput.getText());
		    		rotationMatrix[3] = rotBottomRight;
		    	}

		    	for(int i=0;i<3;i++){  
		    		for(int j=0;j<4;j++){  
		    			for(int k=0;k<4;k++){
		    				outputMatrix[i]+=(lengthMatrix[i][k]*rotationMatrix[k])/4 ;
		    			}
		    		}
		    	}
		    	if(rotTopRightInput.getText().isEmpty()) {
		    		omega = rateOfRotation;
		    		Vx = speed * Math.sin(Math.toDegrees(direction));
		    		Vz = speed * Math.cos(Math.toDegrees(direction));
		    	}
		    	
		    	if(!rotTopRightInput.getText().isEmpty()) {
		    		for(int i = 0; i<outputMatrix.length; i++) {
			    		outputMatrix[i] = .25*outputMatrix[i];
			    	}
			    	Vx = outputMatrix[0];
			    	Vz = outputMatrix[1];
			    	omega = outputMatrix[2];
		    	}
		    	
		    	if(wayPointX != 0 || wayPointY!=0) {
		    		Vx = ((wayPointY-450)/15);
		    		Vz = ((wayPointX-225)/15);
		    	}
		    	vXLabel.setText("Vx: " + Vx + " ft/s");
		    	vZLabel.setText("Vz: " + Vz + " ft/s");
		    	omegaLabel.setText("Omega: " + omega);
		    }  
		});  
		
		autonomous.addActionListener(new ActionListener(){  
		    public void actionPerformed(ActionEvent e){  
		    	resetNeeded = false;
		    	rotationMatrix[0] = 0;
		    	rotationMatrix[1] = 0;
		    	rotationMatrix[2] = 0;
		    	rotationMatrix[3] = 0;
		    	new Timer(100, new ActionListener() {
		    		final long startTime = System.currentTimeMillis();
				      @Override
				      public void actionPerformed(ActionEvent e) {
				    	  if(!resetTimer) {
				    		  timeLabel.setText("Time: " + (System.currentTimeMillis() - startTime)/1000 + " seconds");
				    	  }
				      }
				    }).start();
		    	resetTimer = false;
		    }  
		});  
		reset.addActionListener(new ActionListener(){  
		    public void actionPerformed(ActionEvent e){  
		    	speedInput.setText("");
		    	directionInput.setText("");
		    	rateOfRotationInput.setText("");
		    	rotTopRightInput.setText("");
		    	rotTopLeftInput.setText("");
		    	rotBottomRightInput.setText("");
		    	rotBottomLeftInput.setText("");
		    	Vx = 0;
		    	Vz = 0;
		    	omega = 0;
		    	resetNeeded = true;
		    	vXLabel.setText("Vx: " + Vx + " ft/s");
		    	vZLabel.setText("Vz: " + Vz + " ft/s");
		    	omegaLabel.setText("Omega: " + omega);
		    	timeLabel.setText("Time: " + 0 + " seconds");
		    	resetTimer = true;
		    }  
		});
		stop.addActionListener(new ActionListener(){  
		    public void actionPerformed(ActionEvent e){  
		    	Vx = 0;
		    	Vz = 0;
		    	omega = 0;	    	
		    }  
		});
		
		return inputPanel;				
	}
	
	public class Robot extends Path2D.Double {

        public double angle = 0;
        public double x = 0;
        public double y = 0;
        public double initX = 225;
        public double initY = 450;
        public double topRightX = 255;
        public double topRightY = 510;
        public double topLeftX = 195;
        public double topLeftY = 510;
        public double bottomRightX = 255;
        public double bottomRightY = 390;
        public double bottomLeftX = 195;
        public double bottomLeftY = 390;
        public double topRightRot = 0;
        public double topLeftRot = 0;
        public double bottomRightRot = 0;
        public double bottomLeftRot = 0;
        //15 = .5 feet
        public double wheelRadius = 30;
        public boolean resetRobot = false;
        
        Path2D.Double axis = new Path2D.Double();

        public Robot(int width, int height) {

            moveTo(width, height);
            lineTo(width, height*2);
            lineTo(width*2, height*2);
            lineTo(width*2, height);
            lineTo(width, height);
            closePath();

        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }
        public double getInitX() {
            return this.initX;
        }

        public double getInitY() {
            return this.initY;
        }

        public void setInitX(double d) {
            this.initX = d;
        }

        public void setInitY(double d) {
        	this.initY = d;
        }

        public void moveLocationBy(int x, int y) {
            this.x += x;
            this.y += y;
        }

        public void rotateByDegrees(double delta) {
            angle += delta;
        }

        public void setLocation(double d, double e) {
            this.x = d;
            this.y = e;
        }
        
        public void setAngle(double angle) {
        	this.angle = angle;
        }

        public Shape getTransformedInstance() {
            AffineTransform at = new AffineTransform();
            at.rotate(Math.toRadians(angle), initX, initY);
            at.translate(x, y);
            return createTransformedShape(at);
            
        }        
	}
	
	public class TestPane extends JPanel {

        private Robot robot;
        boolean initialize = true;
        public TestPane() {
        	//15 = .5 feet
        	robot = new Robot(60, 120);
        	robot.setAngle(omega);
        	robot.setLocation(0,0);
        	
        	this.addMouseListener(new MouseListener() {
        	    @Override
        	    public void mouseClicked(MouseEvent e) {
        	    	wayPointX=e.getX();
                    wayPointY=e.getY();
                    wayXLabel.setVisible(true);
                    wayYLabel.setVisible(true);
                    wayYLabel.setText("Waypoint Y Coord: " + (wayPointY-450)/15 + " feet");
                    wayXLabel.setText("Waypoint X Coord: " + (wayPointX-225)/15 + " feet");
                    wayPointSelected = true;
                    //System.out.println("Selected waypoints : x: " + (wayPointX-225)/15+ " y: " + (wayPointY-450)/15);
        	    }
				@Override
				public void mousePressed(MouseEvent e){}
				@Override
				public void mouseReleased(MouseEvent e){}
				@Override
				public void mouseEntered(MouseEvent e){}
				@Override
				public void mouseExited(MouseEvent e){}
        	});

        	Timer timer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	if(resetNeeded) {
                		robot.setLocation(0, 0);
                    	robot.moveLocationBy(135,270);
                    	robot.setAngle(0);
                    	robot.setInitX(225);
                    	robot.setInitY(450);
                    	robot.topRightX = 255;
                        robot.topRightY = 510;
                        robot.topLeftX = 195;
                        robot.topLeftY = 510;
                        robot.bottomRightX = 255;
                        robot.bottomRightY = 390;
                        robot.bottomLeftX = 195;
                        robot.bottomLeftY = 390;
                	}
                	double x = robot.getX();
                    double y = robot.getY();
                    double initX = robot.getInitX();
                    double  initY = robot.getInitY();
                    robot.setLocation(x+=Vz, y+=Vx);
                    robot.setInitX(initX+=Vz);
                    robot.setInitY(initY+=Vx);
                    robot.bottomLeftX+=Vz;
                    robot.bottomLeftY+=Vx;
                    robot.bottomRightX+=Vz;
                    robot.bottomRightY+=Vx;
                    robot.topLeftX+=Vz;
                    robot.topLeftY+=Vx;
                    repaint();
                    if(robot.getX() > 300) {
                    	robot.setLocation(0, 0);
                    	robot.moveLocationBy(135,270);
                    	robot.setAngle(0);
                    	robot.setInitX(225);
                    	robot.setInitY(450);
                    	robot.topRightX = 255;
                        robot.topRightY = 510;
                        robot.topLeftX = 195;
                        robot.topLeftY = 510;
                        robot.bottomRightX = 255;
                        robot.bottomRightY = 390;
                        robot.bottomLeftX = 195;
                        robot.bottomLeftY = 390;
                    }else if(robot.getY() > 600) {
                    	robot.setLocation(0, 0);
                    	robot.moveLocationBy(135,270);
                    	robot.setAngle(0);
                    	robot.setInitX(225);
                    	robot.setInitY(450); 
                    	robot.topRightX = 255;
                        robot.topRightY = 510;
                        robot.topLeftX = 195;
                        robot.topLeftY = 510;
                        robot.bottomRightX = 255;
                        robot.bottomRightY = 390;
                        robot.bottomLeftX = 195;
                        robot.bottomLeftY = 390;
                    }else if(robot.getX() < -20) {
                    	robot.setLocation(0, 0);
                    	robot.moveLocationBy(135,270);
                    	robot.setAngle(0);
                    	robot.setInitX(225);
                    	robot.setInitY(450);
                    	robot.topRightX = 255;
                        robot.topRightY = 510;
                        robot.topLeftX = 195;
                        robot.topLeftY = 510;
                        robot.bottomRightX = 255;
                        robot.bottomRightY = 390;
                        robot.bottomLeftX = 195;
                        robot.bottomLeftY = 390;
                    }else if(robot.getY() < -80) {
                    	robot.setLocation(0, 0);
                    	robot.moveLocationBy(135,270);
                    	robot.setAngle(0);
                    	robot.setInitX(225);
                    	robot.setInitY(450);
                    	robot.topRightX = 255;
                        robot.topRightY = 510;
                        robot.topLeftX = 195;
                        robot.topLeftY = 510;
                        robot.bottomRightX = 255;
                        robot.bottomRightY = 390;
                        robot.bottomLeftX = 195;
                        robot.bottomLeftY = 390;
                    }else if(x/15 == (wayPointX-225)/15|| y/15 == (wayPointY-450)/15) {
                    	System.out.println(x/15 + " " + (wayPointX-225)/15);
                    	System.out.println(y/15 + " " + (wayPointY-450)/15);
                    	Vx = 0;
                    	Vz = 0;
                    }
                    
                    robot.rotateByDegrees(omega);
                }
            });
            timer.start();

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(450, 900);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //15 = .5 feet
            for(int i = 0; i <= 3000; i+=30){
                for(int j = 0; j <= 3000; j+=30){
                    g.clearRect(i, j, 15, 15);
                }
            }
            
            for(int i = 15; i <= 3050; i+=30){
                for(int j = 15; j <= 3050; j+=30){
                    g.clearRect(i, j, 15, 15);
                }
            }
            Graphics2D g2d = (Graphics2D) g.create();        
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Shape shape = robot;
        
            if(initialize) {
            	robot.moveLocationBy(135,270);
            	initialize = false;
            }
            shape = robot.getTransformedInstance();
            g2d.setColor(Color.BLUE);
            g2d.fill(shape);
            g2d.setColor(Color.RED);
            g2d.draw(new Line2D.Double(robot.initX,robot.initY,robot.initX+60,robot.initY));
            g2d.draw(new Line2D.Double(robot.initX,robot.initY,robot.initX,robot.initY+90));
            //g2d.draw(new Line2D.Double(robot.initX,robot.initY,shape.getBounds().x+90,shape.getBounds().y+60));
            //g2d.draw(new Line2D.Double(robot.initX,robot.initY,shape.getBounds().x,shape.getBounds().y));
            g2d.draw(shape);
            g2d.setColor(Color.GREEN);  
            if(wayPointSelected) {
            	if(!resetNeeded) {
            		g2d.draw(new Line2D.Double(225,450,wayPointX,wayPointY));
            	}           	
            }else {
            	g2d.draw(new Line2D.Double(225,450,robot.initX,robot.initY));
            }
            g2d.dispose();
            

        }

    }

}
