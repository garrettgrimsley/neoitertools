package net.ericaro.neoitertools;

/** A simple immutable object used to handle both the index and the object
 * @author eric
 *
 * @see <a href="http://code.google.com/p/neoitertools/wiki/Index">Index's wiki page</a>
* @see <a href="http://code.google.com/p/neoitertools/">neoitertools site</a>
 */
public class Index<T> {

	public final int i;
	public final T value;
	
	public Index(int i, T value) {
		this.i = i;
		this.value = value;
	}

	@Override
	public String toString() {
		return i+":"+String.valueOf(value);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Index) {
			Index that = (Index) other;
			return i== that.i && ( value==that.value || value.equals(that.value)) ; 
		}
		return false;
	}

	
	
	
	
	
}
