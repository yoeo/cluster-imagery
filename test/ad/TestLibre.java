/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ad;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ydeo
 */
public class TestLibre {

    public TestLibre() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    public void hello()
	{
		System.out.println("un message");
	}


	class sousClasse extends Thread
	{
		public void calculer ()
		{
			hello();
		}

		@Override
		public void run()
		{
			calculer();
		}

	}

	@Test
	public void testRandom ()
	{
		for(int i=0;i<20;i++)
		{
			int v = (int) ((4 - 2 + 1) * Math.random() + 2);
			Log.Log("" + v);
		}
	}
	
	@Test
	public void lanceSousClasse()
	{
		sousClasse c = new sousClasse();
		c.start();//calculer();
	}

}