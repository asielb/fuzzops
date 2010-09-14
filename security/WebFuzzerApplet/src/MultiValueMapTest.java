import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;


public class MultiValueMapTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
		MultiMap mvm = MultiValueMap.decorate(hm);
		
		mvm.put(1, 1);
		mvm.put(1, 2);
		mvm.put(2, 1);
		mvm.put(2, 3);
		mvm.put(3, 1);
		
		System.out.println("MultiValueMap: " + mvm);
		
		for (Iterator iter = mvm.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Map.Entry)iter.next();
	        Integer key = (Integer) entry.getKey();
	        System.out.format("Key: %d\n", key);
	        
	        Collection vals = (Collection) entry.getValue();
	        for (Iterator valIter = vals.iterator(); valIter.hasNext(); ) {
	        	Integer num = (Integer) valIter.next();
	        	System.out.format("\tVal: %d\n", num);
	        }
		}
	}

}
