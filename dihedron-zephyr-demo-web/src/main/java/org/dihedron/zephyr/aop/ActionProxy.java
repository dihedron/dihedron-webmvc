/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 *
 * This file is part of the Zephyr framework ("Zephyr").
 *
 * Zephyr is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Zephyr is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Zephyr. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.zephyr.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * At deployment time the framework will generate a new class (a "proxy") for each 
 * annotated action provided by the user; the proxy class provides two facilities:<ol> 
 * <li>a factory method (<b>action factory</b>) capable of providing an instance 
 * of the user-provided class without resorting to reflection, and possibly 
 * recycling a single instance across invocations (if the business class proves 
 * to be stateless, that is it has no instance fields that can be used to store 
 * data across invocations)</li>
 * <li>a set of stub methods (<b>method stubs</b>), one for each 
 * {@code &atInvocable}-annotated business method in the original user's class, 
 * capable of picking input parameters from the various scopes ("unmarshalling"), 
 * validating them, invoking the original user's business method, then picking 
 * its results, validating them and storing them back into the proper scopes 
 * ("marshalling") before giving control back to the framework.</li></ol>
 * Both facilities use no reflection at all at runtime, in order to guarantee 
 * the same performances that would be attained were the code hand-written by 
 * the user.
 *  
 * @author Andrea Funto'
 */
public class ActionProxy {

    /**
     * The dynamically-generated and class loader-injected proxy class, exposing
     * one static method for each invocable action method, plus a factory method
     * to create instances of the user-provided business action class without 
     * resorting to reflection.
     */
    private Class<?> proxyClass;

    /**
     * A factory method that will take care of providing an instance of the 
     * user-implemented class containing the invocable business methods; this method
     * does not resort to reflection to instantiate the objects and may even decide
     * to recycle the same object instance across invocations, for the sake of
     * efficiency and performance, if the user-provided business class does not
     * have any instance fields.
     */
    private Method actionFactory;

    /**
     * A map of original, user-provided business methods to their corresponding
     * static stub methods.
     */
    private Map<Method, Method> stubMethods = new HashMap<>();

    /**
     * Constructor with package visibility, so it cannot be instantiated outside
     * of the current package.
     */
    ActionProxy() {
    }

    /**
     * Returns the synthetically-generated {@code Class} object of the proxy.
     *
     * @return 
     *   the {@code Class} of the proxy, as created by the framework at deployment
     *   time.
     */
    public Class<?> getProxyClass() {
        return proxyClass;
    }

    /**
     * Sets the {@code Class} object of the proxy.
     *
     * @param proxyClass 
     *   the {@code Class} object of the synthetic proxy.
     */
    void setProxyClass(Class<?> proxyClass) {
        this.proxyClass = proxyClass;
    }

    /**
     * Returns the factory method that will instantiate a new instance of the 
     * user-provided on which the actual business method resides without resorting 
     * to reflection, for the sake of high performances.
     *
     * @return
     *   the factory method to allocate a new instance of the user-provided 
     *   business class.
     */
    public Method getActionFactory() {
        return actionFactory;
    }

    /**
     * Sets the factory method that will provide a new instance of the user-provided
     * action class without resorting to reflection; depending on the nature of
     * the business class (e.g. whether it is stateful, has instance fields etc.)
     * the provider may even decide to recycle a single instance across invocations. 
     *
     * @param provider 
     *   the user-provided business class factory method.
     */
    void setActionFactory(Method provider) {
        this.actionFactory = provider;
    }

    /**
     * Returns the methods map, providing a stub method for each invocable method
     * in the original user-provided business action class.
     *
     * @return 
     *   a map of original business method to framework-generated stub methods.
     */
    public Map<Method, Method> getStubMethods() {
        return stubMethods;
    }

    /**
     * Sets the stub methods map, providing a static, framework-generated stub 
     * method for each invocable method in the original user-provided business 
     * action class.
     *
     * @param stubMethods 
     *   the methods map, having the original business methods as keys and their
     *   corresponding, framework-generated static stubs as values.
     */
    void setStubMethods(Map<Method, Method> stubMethods) {
        this.stubMethods = stubMethods;
    }
}
