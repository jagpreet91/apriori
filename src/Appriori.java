import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;

public class Appriori 
{
	public static void main(String args[])
	{
		
		double transactions = 0;
		BufferedReader br = null;
		String Line;
		double support = new Double(args[0]);
		double confidence = new Double(args[1]);
		System.out.println("Minimum Support = "+support+"%");
		System.out.println("Minimum Confidence = "+confidence+"%");
		support = support/100;
		confidence = confidence/100;
		System.out.println();
		HashMap <String,Integer> items = new HashMap<String,Integer>();
		TreeSet<String> itemset = new TreeSet<String>();
		HashMap <String,Integer> frequent_itemset = new HashMap<String,Integer>();
		HashMap <String,ArrayList<Integer>> lookup = new HashMap<String,ArrayList<Integer>>();
		TreeSet <String> associations = new TreeSet<String>();
		ArrayList <Integer> al;
		int counter=1;
		try
		{
			br = new BufferedReader(new FileReader("C:\\Users\\Jagpreet Singh\\EEworkspace\\DataMining\\src\\apriori_data_set.txt"));
			try 
			{
				while((Line = br.readLine())!=null)
				{	
					String[] lineSplit = Line.split(",");
					for(int i=0;i<lineSplit.length;i++)
					{
						if(items.containsKey(lineSplit[i]))
						{
							items.put(lineSplit[i], items.get(lineSplit[i])+1);
							al = new ArrayList<Integer>();
							al = lookup.get(lineSplit[i]);
							al.add(counter);
							lookup.put(lineSplit[i], al);
						}
						else
						{
							items.put(lineSplit[i],1);
							al = new ArrayList<Integer>();
							al.add(counter);
							lookup.put(lineSplit[i],al);
							itemset.add(lineSplit[i]);
						}
					}
					counter++;
					transactions++;
				}
				String itemset_arr []= new String[itemset.size()];
				itemset.toArray(itemset_arr);
				check_support(items,itemset_arr,transactions,support,itemset);
				frequent_itemset.putAll(items);
				boolean ret_value = true;
				int concat_index = 0;
				while(ret_value==true)
				{
					ret_value = concat_items(items,itemset_arr,transactions,support,itemset,lookup,concat_index);
					String con_arr[] = new String[itemset.size()];
					itemset.toArray(con_arr);
					scan_items(items,con_arr,transactions,support,itemset,lookup);
					check_support(items,con_arr,transactions,support,itemset);
					frequent_itemset.putAll(items);
					concat_index++;
				}
				System.out.println();
				System.out.println("---------------------------------");
				System.out.println("Frequent Itemset:");
				for (Entry<String, Integer> entry : frequent_itemset.entrySet()) 
				{
					itemset.add(entry.getKey());
					double support_val = 0;
					
					support_val = (double)(entry.getValue())/transactions;
					System.out.println("ITEMSET: " + entry.getKey() + " FREQUENCY : " + entry.getValue()+ " SUPPORT : "+ support_val);
				};		
				String con_arr[] = new String[itemset.size()];
				itemset.toArray(con_arr);
				String temp_arr[] = new String[itemset.size()];
				itemset.toArray(temp_arr);
				System.out.println();				
				HashMap<String,Integer> rules = new HashMap<String,Integer>();
				for(int i=0;i<itemset.size();i++)
				{
					String[] lineSplit = con_arr[i].split(",");
					if(lineSplit.length>1)//because individual itemset can't create association rules
					{
						for(int j=0;j<lineSplit.length;j++)
						{
							for(int k=j+1;k<lineSplit.length;k++)
							{
								rules.put(lineSplit[j]+","+lineSplit[k],2);
								rules.put(lineSplit[j], 2);
								rules.put(lineSplit[k], 2);
								
							}
						}
						rules.remove(temp_arr[i]);
						String str1 = temp_arr[i];
						for (Entry<String, Integer> entry : rules.entrySet()) 
						{
							String str2 = entry.getKey();
							String str3 = entry.getKey()+",";
							String str4 = ","+entry.getKey();
							String str5 = ","+entry.getKey()+",";
							if(str1.toLowerCase().contains(str5.toLowerCase()))
							{
								str1 = str1.replace(str5, ",");
							}
							else if(str1.toLowerCase().contains(str3.toLowerCase()))
							{
								str1 = str1.replace(str3, "");
							}
							else if(str1.toLowerCase().contains(str4.toLowerCase()))
							{
								str1 = str1.replace(str4, "");
							}
							else if(str1.toLowerCase().contains(str2.toLowerCase()))
							{
								str1 = str1.replace(str2, "");
							}
							else
							{
								String[] comma = str2.split(",");
								for(int m=0;m<comma.length;m++)
								{
									if(str1.contains(","+comma[m]+","))
									{
										str1 = str1.replace(","+comma[m]+",", ",");
									}
									else if(str1.contains(comma[m]+","))
									{
										str1 = str1.replace(comma[m]+",", "");
									}
									else if(str1.contains(","+comma[m]))
									{
										str1 = str1.replace(","+comma[m], "");
									}
									else if(str1.contains(comma[m]))
									{
										str1 = str1.replace(comma[m], "");
									}
									else
									{
										System.out.println("Can't go here... this else condition must never be hit!  "+temp_arr[i]);
									}
								}								
							}							
							if(str1.length()==0)
							{
								System.out.println("empty temp_string");
							}
							double support_count_I = frequent_itemset.get(temp_arr[i]);
							double support_count_s = frequent_itemset.get(entry.getKey());
							if((support_count_I/support_count_s)>=confidence)
							{
								associations.add(entry.getKey()+"--->"+str1);
							}
							str1 = temp_arr[i];
						};
					}
					rules.clear();					
				}
				System.out.println("Association Rules:");
				System.out.println("---------------------------------");
				String allrules[] = new String[associations.size()];
				associations.toArray(allrules);
				for(int i=0;i<allrules.length;i++)
				{
					System.out.println(allrules[i]);
				}
			}
			catch (IOException e) 
			{
				System.out.println("Can't Read File");
				System.exit(0);
			}

		}
		catch(FileNotFoundException fnfex)
		{
			System.out.println("Invalid File Name");
			System.exit(0);
		}
	}
	public static void check_support(HashMap<String,Integer>data,String arr[],double trans,double supp,TreeSet<String>products)
	{
		for(int i=0;i<arr.length;i++)
		{
			if((data.get(arr[i])/trans)<supp)
			{
				data.remove(arr[i]);
				products.remove(arr[i]);
			}
		}
	}
	public static boolean concat_items(HashMap<String,Integer>data,String arr[],double trans,double supp,TreeSet<String>products,HashMap<String,ArrayList<Integer>>lookup,int ind)
	{
		String arr2 []= new String[products.size()];
		
		products.toArray(arr2);
		products.removeAll(products);
		data.clear();
		if(ind==0)
		{
			String element = "";
			for(int i=0;i<arr2.length;i++)
			{
				for(int j=i+1;j<arr2.length;j++)
				{
					element = arr2[i]+","+arr2[j];
					products.add(element);
					data.put(element, 2);
				}
			}
		}
		else
		{			
			int ctr = 0;
			
			HashMap <Integer,String> temp_map = new HashMap<Integer,String>();
			TreeSet <String> temp_set = new TreeSet<String>();
			String element = "";
			for(int i=0;i<arr2.length;i++)
			{
				element =arr2[i]+",";
				for(int j=i+1;j<arr2.length;j++)
				{
					element+=arr2[j];
					temp_map.put(ctr, element);
					element=arr2[i]+",";
					ctr++;
				}				
			}
			for(int i=0;i<temp_map.size();i++)
			{
				String line = temp_map.get(i);
				String[] lineSplit = line.split(",");
				for(int j=0;j<lineSplit.length;j++)
				{
					temp_set.add(lineSplit[j]);
				}
				if(temp_set.size()==ind+2)
				{
					String temp_arr[] = new String[temp_set.size()];
					temp_set.toArray(temp_arr);
					String extra="";
					for(int k=0;k<temp_set.size();k++)
					{
						extra+=temp_arr[k]+",";
					}
					temp_set.removeAll(temp_set);
					extra = extra.substring(0, extra.length() - 1);
					products.add(extra);
					data.put(extra, 2);
				}
				else
					temp_set.removeAll(temp_set);
			}
		}
		if(data.size() == 0)
			return false;
		else
			return true;
	}
	public static void scan_items(HashMap<String,Integer>data,String arr[],double trans,double supp,TreeSet<String>products,HashMap<String,ArrayList<Integer>>lookup)
	{
		String[] lineSplit=null;
		for(int i=0;i<arr.length;i++)
		{
			lineSplit = arr[i].split(",");
			ObjectList ob[] = new ObjectList[lineSplit.length];
			for(int j=0;j<lineSplit.length;j++)
			{
				ob[j] = new ObjectList();
				ob[j].setListVariable(lookup.get(lineSplit[j]));
			}
			for(int k=1;k<ob.length;k++)
			{
				ob[0].al_obj.retainAll(ob[k].al_obj);
			}
			data.remove(data.get(arr[i]));
			data.put(arr[i], ob[0].al_obj.size());
		}
	}
}