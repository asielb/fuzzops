package jFuzz;

public class FuzzRnd {

	private static final int MAX_SEED_SIZE_IN_BYTES = 256;
	private static final int S_BOX_LENGTH = 256;
	private static final int DEFAULT_DROP_COUNT = 1024;

	private int drop_count;
	private int[] s;
	private int i;        // State counter 0
	private int j;        // State counter 1

	public FuzzRnd() {

		this( (byte [])null );
	}

	public FuzzRnd( byte[] seed ) {

		this( seed, DEFAULT_DROP_COUNT );
	}
	
	public FuzzRnd( String seed ) {

		this( seed.getBytes(), DEFAULT_DROP_COUNT );
	}

	public FuzzRnd( byte[] seed, int d ) {

		drop_count = d;
		s = new int[ S_BOX_LENGTH ];
		setSeed( seed );
	}

	void generateBytes( byte[] return_value ) {

		int t;

		for ( int current_byte = 0 ; current_byte < return_value.length ; current_byte++ ) {

			i = ( i + 1 ) % S_BOX_LENGTH;
			j = ( j + s[ i ] ) % S_BOX_LENGTH;

			t = s[ i ];
			s[ i ] = s[ j ];
			s[ j ] = t;

			t = ( s[ i ] + s[ j ] ) % S_BOX_LENGTH;
			return_value[ current_byte ] = (byte)s[ t ];
		}
	}

	public static int getMaxSeedSize() {

		return MAX_SEED_SIZE_IN_BYTES;
	}

	void initialize( byte[] seed ) {

		int[] k= new int[ S_BOX_LENGTH ];

		// Initialize the S-box.
		for ( i = 0 ; i < S_BOX_LENGTH ; i++ ) {
			s[ i ] = i;
		}

		j = 0;

		// Fill k with seed, repeating the seed as necessary to fill the entire array.
		for ( i = 0 ; i < S_BOX_LENGTH ; i++ ) {
			k[ i ] = (int)seed[ j ] & 0xff;
			j = ( j + 1 ) % seed.length;
		}

		j = 0;

		for ( i = 0 ; i < S_BOX_LENGTH ; i++ ) {

			int t;

			j = ( j + s[ i ] + k[ i ] ) % S_BOX_LENGTH;

			t = s[ i ];
			s[ i ] = s[ j ]; 
			s[ j ] = t;
		}

		// Initialize the state variables for the generator function.
		i = 0;
		j = 0;

		// Drop requested number of initial bytes.
		if ( drop_count > 0 ) {
			nextBytes( new byte[ drop_count ] );
		}
	}

	public void nextBytes( byte[] return_value ) {

		if (( return_value != null ) && ( return_value.length >= 1 )) {
			generateBytes( return_value );
		}
	}

	public void setSeed() {

		setSeed( (byte[])null );
	}

	public void setSeed( byte[] seed ) {

		if (( seed == null ) || ( seed.length < 1 )) {
			// User didn't care enough to specify a seed, so create
			// a quick and dirty non-null, non-zero length default seed.
			seed = new project.prng.seed.Default().getValue();
		}

		initialize( seed );
	}

	
}
