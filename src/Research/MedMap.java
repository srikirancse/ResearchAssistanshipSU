package Research;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class MedMap implements Cloneable {
	
	double patients[][]=new double[1000010][2];
	double doctors[][]=new double[100010][2];
	int  patientcount=0;
	int doctorcount=0;
	boolean isPatientAssigned[]=new boolean[1000010];
	final int docCap[] = new int[100010];
	double x=0;
	double y=0;
	Random rand=new Random();
	static PrintStream out1;
	
	
	public void circleGenerator(float x1,float y1, float r)
	{	
		System.out.println("Doctors:");
		for(int i=0;i<1000000;i++)
		{
			x=rand.nextGaussian()*40;
			y=rand.nextGaussian()*40;
			if(((x-x1)*(x-x1))+((y-y1)*(y-y1))<r*r)
			{
				doctors[doctorcount][0]=x;
				doctors[doctorcount][1]=y;
				int c=rand.nextInt(15);
				docCap[doctorcount]=c;
				System.out.println(doctors[doctorcount][0]+" "+doctors[doctorcount][1]);
				System.out.println("Doctor"+ doctorcount + "capacity: "+ c);
				doctorcount++;
			}
		}
		System.out.println("Doctor count:"+doctorcount); //changed to out
		//System.out.println("Patients:");
		for(int i=0;i<100000;i++)
		{
			x=rand.nextGaussian()*40;
			y=rand.nextGaussian()*40;
			if(((x-x1)*(x-x1))+((y-y1)*(y-y1))<r*r)
			{
				patients[patientcount][0]=x;
				patients[patientcount][1]=y;
				//System.out.println(patients[patientcount][0]+" "+patients[patientcount][1]);
				patientcount++;
			}
		}
		System.out.println("Patient count:"+patientcount); //changed to out
		
	}
	
	public void DispatchNow()
	{
		
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			out1 = new PrintStream(new FileOutputStream("C://Users/Srikiran Sistla/Desktop/output.txt"));
			System.setOut(out1);
		} catch (Exception e) {
			System.out.println(e);
		}
		MedMap m=new MedMap();
		
		m.circleGenerator(4,6,3);
		//System.out.println(Haversine.haversine(17.3700,78.4800,43.0469,283.8556));
		//System.out.println(m.patients[1][0]+" "+m.patients[1][1]);
	}

}
