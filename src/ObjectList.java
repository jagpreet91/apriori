import java.util.ArrayList;
public class ObjectList 
{
	public ArrayList <Integer>al_obj;
	public void setListVariable(ArrayList<Integer> temp)
	{
		al_obj = new ArrayList<Integer>();
		for(int i=0;i<temp.size();i++)
		{
			al_obj.add(temp.get(i));
		}
	}
}
