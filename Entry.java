public class Entry {
	public String key;
	public int value;
	
	public Entry(String k,int v) {
		key=k;
		value=v;
	}
	
	public String toString() {
		return "(" + key + "," + value + ")";
	}
}