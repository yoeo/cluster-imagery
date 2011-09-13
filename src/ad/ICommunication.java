/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ad;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ydeo
 */
public interface ICommunication extends Remote
{
	public abstract void recevoir(Jeton j) throws RemoteException;
}
