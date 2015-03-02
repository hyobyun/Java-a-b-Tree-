public class Node {
	public Entry[] entries;
	public Node[] children;
	public Node parent;
	public Entry ofEntry;
	public Node ofChild; 
	
	public Node(int b) {
		entries = new Entry[b-1];
		children = new Node[b];
		ofEntry=null;
		ofChild=null;
		for (int i=0;i<entries.length;i++)
			entries[i]=null;
		for (int i=0;i<children.length;i++)
			children[i]=null;
		parent=null;
	}
	public int countEntries() {
		int i=0;
		while(i<entries.length&& entries[i]!=null)
			i++;
		return i;
	}
	public String toString() {
		String rtn="";
		for (int i=0; i<entries.length;i++) 
			rtn=rtn+(entries[i]!=null ? entries[i] : "(-)");
			rtn=rtn+" O:" +(ofEntry!=null? ofEntry : "(-)");
		return rtn;
	}
	
	public Object find(String k) {
		for(int i=0;i<entries.length;i++) {
			if(entries[i]==null)
				return children[i];
			int cmp=k.compareTo(entries[i].key);
			if(cmp==0) 
				return entries[i];
			else if (cmp<0)
				return children[i];
		}
		return children[children.length-1];
	}
	public Integer getEntryPosition(String k) {
		for(int i=0;i<entries.length;i++) 
			if(entries[i].key.compareTo(k) ==0)
				return(i);
		return null;
	}
	public void insertEntry(Entry e) {
		for(int i=0;i<entries.length;i++) {
			if(entries[i]==null || e.key.compareTo(entries[i].key)<0) {
				shiftEntries(i);
				entries[i]=e;
				return;
			}
		}
		ofEntry=e;
	}
	public void insertEntry(Entry e,Node c) {
		for(int i=0;i<entries.length;i++) {
			if(entries[i]==null || e.key.compareTo(entries[i].key)<0) {
				shiftEntries(i);
				entries[i]=e;
				children[i+1]=c;
				return;
			}
		}
		ofEntry=e;
		ofChild=c;
	}
	
	public void shiftEntries(int i) {
		for(int j=entries.length;j>i;j--) {
			if(j==entries.length){
				ofEntry = entries[j-1]; 
				ofChild = children[j];
			} else {
				entries[j]=entries[j-1];
				children[j+1]=children[j];
			}
		}
	}
	
	public void removeEntry(int p) {
		for(int i=p;i<entries.length-1;i++) {
			entries[i]=entries[i+1];
		}
		entries[entries.length-1]=null;
	}
	
	public int findNodePos(Node n) {
		for(int i=0;i<children.length;i++)
			if(children[i].equals(n))
				return i;
		return -1;
	}
	public Node leftSibling(Node n) {
		int pos=findNodePos(n);
		if(pos>0)
			return children[pos-1];
		else
			return null;
	}
	public Node rightSibling(Node n) {
		int pos=findNodePos(n);
		
		if(pos<children.length-1)
			return children[pos+1];
		else
			return null;
	}	
	
}