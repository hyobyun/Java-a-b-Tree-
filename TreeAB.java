/* Hyo Byun */
import java.util.*;

public class TreeAB {
	public int a;
	public int b;
	public Node root;
    public Node searchEnd;
	
	public TreeAB(int ai, int bi) {
		a=ai;
		b=bi;
		root = new Node(b);  
	}
	
	public void put(String k, int v) {
		Entry e = new Entry(k,v);
		Node cur=root;
		Node prev=null;
		
		do {
			Object rtn = cur.find(k);
			//found key
			if(rtn instanceof Entry)  {
				((Entry)rtn).value=v;
				return;
			}
			prev=cur;
			cur=((Node)rtn);
		} while (cur !=null);
		prev.insertEntry(e);
		handleOverflow(prev);
	}
	
	public void handleOverflow(Node cur) {
		if(cur.ofEntry==null)
			return;
		int mid = b/2;
		Node carry = new Node(b);
		
		int j=0;
		for(int i=mid+1;i<b-1;i++) {
			carry.entries[j]=cur.entries[i];
			cur.entries[i]=null;
			j++;
		}
		carry.entries[j]=cur.ofEntry;
		cur.ofEntry=null;
		
		j=0;
		for (int i=mid+1;i<b;i++) {
			if(cur.children[i]==null)
				break;
			carry.children[j]=cur.children[i];
			carry.children[j].parent=carry;
			cur.children[i]=null;
			j++;
		}
		if (cur.ofChild!=null) {
			carry.children[j]=cur.ofChild;
			carry.children[j].parent=carry;
			cur.ofChild=null;
		}
		if(cur.parent==null) {
			Node newRoot = new Node(b);
			root=newRoot;
			cur.parent=root;
			root.children[0]=cur;
		}
		carry.parent= cur.parent;
		cur.parent.insertEntry(cur.entries[mid], carry);
		cur.entries[mid]=null;
		handleOverflow(cur.parent);
		handleUnderflow(cur.parent);
	}
	
	
	
	
	
	
	public Integer remove(String k) {
		Node cur=root;
		
		do {
			Object rtn = cur.find(k);
			//found key
			if(rtn instanceof Entry)
				break;
			if(rtn==null)
				return null;
			cur=((Node)rtn);
		} while (cur !=null);
		int pos=cur.getEntryPosition(k); //FIX
		
		Node oldNode= cur;
		Entry oldEntry = cur.entries[pos];
		
		int rightChildPos=pos+1;
		if(cur.children[rightChildPos]==null) { //is leaf
			cur.removeEntry(pos);
			handleUnderflow(cur);
			return oldEntry.value;
		}
			cur=cur.children[rightChildPos];
		while(cur.children[0]!=null) //Find successor
			cur = cur.children[0];
		oldNode.entries[pos]=cur.entries[0];
		cur.removeEntry(0);
		handleUnderflow(cur);
//		printTree();
		return oldEntry.value;
	}
	
	public void handleUnderflow(Node n) {
		if(n.countEntries()>=(a-1)) {
			return;
		}
			
			
		if(n.equals(root)) {
			System.out.println("ROOOT UNDERFLOW -OK");
			if(n.countEntries()==0)
				root=root.children[0];
			return;
		}
		//Try Transfer
		Node lSib = n.parent.leftSibling(n);
		Node rSib = n.parent.rightSibling(n);
		
		int rootEntryPos= n.parent.findNodePos(n);

		
		if( rSib!=null && rSib.countEntries()>=a) {
			System.out.println("Right Transfer");
			n.entries[n.countEntries()]=n.parent.entries[rootEntryPos];
			n.parent.entries[rootEntryPos] = rSib.entries[0];
			n.children[n.countEntries()]=rSib.children[0];
			rSib.children[0]=null;
			if(n.children[n.countEntries()]!=null)
				n.children[n.countEntries()].parent=n;
			for(int i=0;i<rSib.children.length-1;i++)
				rSib.children[i]=rSib.children[i+1];
			for(int i=0;i<rSib.entries.length-1;i++)
				rSib.entries[i]=rSib.entries[i+1];
			rSib.children[rSib.children.length-1]=null;
			rSib.entries[rSib.entries.length-1]=null;
			return;
		}
		if(lSib!=null && lSib.countEntries()>=a) {
			System.out.println("Left Transfer");
			rootEntryPos--;
			
			//shift everything right
			for(int i=n.children.length-1;i>0;i--)
				n.children[i]=n.children[i-1];
			for(int i=n.entries.length-1;i>0;i--)
				n.entries[i]=n.entries[i-1];
				
			n.entries[0]=n.parent.entries[rootEntryPos]; //move entry down right
			n.parent.entries[rootEntryPos] = lSib.entries[lSib.countEntries()-1]; //move entry up from left

			n.children[0]=lSib.children[lSib.countEntries()];
			lSib.children[lSib.countEntries()]=null;
			lSib.entries[lSib.countEntries()-1]=null;
		
			if(n.children[0]!=null)
				n.children[0].parent=n;
			
			return;
		}
		//Transfer not possible, do merge
		if(rSib!=null) { //right Merge
			System.out.println("RIGHTMERGE");
			int numEntries=n.countEntries()+1;
			for(int i=rSib.children.length-1;i>=numEntries;i--)
				rSib.children[i]=rSib.children[i-numEntries];	
			for(int i=rSib.entries.length-1;i>=numEntries;i--) {
				rSib.entries[i]=rSib.entries[i-numEntries];
			}
			for(int i=0; i<numEntries-1;i++) { //check
				rSib.entries[i]=n.entries[i];
				rSib.children[i+1]=n.children[i+1];
				if(rSib.children[i+1]!=null) 
					rSib.children[i+1].parent=rSib;
				n.children[i+1]=null;
				n.entries[i]=null;
			}
			rSib.entries[numEntries-1]=n.parent.entries[rootEntryPos];
			rSib.children[0]=n.children[0];
			if(rSib.children[0]!=null)
				rSib.children[0].parent=rSib;
				
			for(int i=rootEntryPos; i<n.parent.entries.length-1;i++)
				n.parent.entries[i]=n.parent.entries[i+1];
				
			for(int i=rootEntryPos; i<n.parent.children.length-1;i++)
				n.parent.children[i]=n.parent.children[i+1];
				
			n.parent.entries[n.parent.entries.length-1]=null;
			n.parent.children[n.parent.children.length-1]=null;
			
		} else { //left Merge
			System.out.println("LEFTMERGE");
			int numEntries=n.countEntries();
			lSib.entries[lSib.countEntries()]=n.parent.entries[rootEntryPos-1];
			int temp=lSib.countEntries();
			for(int i=0; i<numEntries;i++) {
				lSib.entries[lSib.countEntries()] =n.entries[i];
			}
			for(int i=0; i<numEntries;i++) {
				lSib.children[temp+i] =n.children[i];
				if(lSib.children[temp+i]!=null)
					lSib.children[temp+i].parent=lSib;
				n.children[i]=null;
			}
			lSib.children[temp+numEntries]=n.children[numEntries];
			if(lSib.children[temp+numEntries]!=null)
				lSib.children[temp+numEntries].parent=lSib;
				
			for(int i=rootEntryPos-1;i<n.parent.entries.length-1;i++)
				n.parent.entries[i]=n.parent.entries[i+1];
			for(int i=rootEntryPos;i<n.parent.children.length-1;i++)
				n.parent.children[i]=n.parent.children[i+1];
		
			n.parent.entries[n.parent.entries.length-1]=null;
			n.parent.children[n.parent.children.length-1]=null;
		}
		handleUnderflow(n.parent);
	}
	
	void padding(String s, int n) {
		for( int i=0;i<n;i++)
			System.out.print(s);
	}
	
    int MaxLevel;
	void printSub(Node p, int id, int level) {
		 if ( level > MaxLevel )
		  MaxLevel = level;
		if (p==null)
			return;
		int end=0;
		while(p.children[end]!=null && end<b-1)
			end++;
		end--;
		int mid=end/2;
		
		for (int i=b-1; i>mid;i--)
			if(p.children[i]!=null)
				printSub(p.children[i],i, level+1);
				
		padding( "             ", level );
		System.out.println("" + id + ":" + p);
		if ( id == 0 && level == MaxLevel )
             System.out.println();
		for (int i=mid; i>=0;i--)
			if(p.children[i]!=null)
				printSub(p.children[i],i, level+1);
	}
	
	public void printTree() {
		printSub(root,0,0);
	}
	

	public void checkTree()
	    {
	       if ( b <= 0 )
	       {
	          System.out.println("Error: Your constructor did not set the value b");
		  return;
	       }

	       boolean error = false;
	       checkSub( root );		// The real check alg. is recursive
	       if ( error )
		  System.exit(1);
	       System.out.println();
	    }

	    public void checkSub (Node p) 
	    {
	       int i;

	       if ( p == null)
	          return;

	       // Check parent child relationship for node
	       for ( i = 0; i < b; i++ )
		  if ( p.children[i] != null )
		  {
		     if ( p.children[i].parent != p )
		     {
		        printTree();
		        System.out.println("---------------------------");
		        System.out.println("Error:");
		        System.out.println("p: " + p);
		        System.out.println("p.child[" + i + "] = " + p.children[i]);
		        System.out.println("BUT: p.child[" + i + "].parent = " 
				+ p.children[i].parent);
	             }
	          }

	       // Recurse
	       for ( i = 0; i < b; i++ )
		  if ( p.children[i] != null )
		     checkSub(  p.children[i] );
	    }

 
}