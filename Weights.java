import java.io.*;

public class Weights
{
	private double[] weights;
	private String path;
	
	public Weights()
	{
		this("weights.txt");
	}
	
	public Weights(String filePath)
	{
		File file = new File(filePath);
		path = filePath;
        BufferedReader reader = null;
		double[] tempWeights = new double[1000];
		int i = 0;
		
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) 
			{
                tempWeights[i] = Double.parseDouble(text);
				i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		weights = new double[i];
        
        for(int x = 0; x < i; x++)
		{
			weights[x] = tempWeights[x];
		}
	}
	
	public void printWeights()
	{
		System.out.println("Weights:");
		System.out.println(toString());
	}
	
	public double[] getWeights()
	{
		return weights;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		int l = weights.length;
		for(int i = 0; i < l; i++)
		{
			if(i == l-1)
				s+=weights[i];
			else
				s+=weights[i]+"\n";
		}
		return s;
	}
	
	public void setWeights(double[] newWeights)
	{
		weights = newWeights;
	}
	
	public void saveWeights()
	{
		try {
		  Writer output = new BufferedWriter(new FileWriter(path));
	      output.write( toString() );
		  output.close();
	    }
		catch(Exception e) {}

	}
	
	/*public static void main(String args[])
	{
		Weights w = new Weights();
		w.printWeights();
		double[] d = w.getWeights();
		for(int x = 0; x < d.length; x++)
		{
			d[x]*=2;
		}
		w.setWeights(d);
		w.saveWeights();
		w.printWeights();
	}*/
}