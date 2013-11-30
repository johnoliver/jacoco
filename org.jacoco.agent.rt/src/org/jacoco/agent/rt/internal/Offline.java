/*******************************************************************************
 * Copyright (c) 2009, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.agent.rt.internal;

import java.util.Properties;

import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.RuntimeData;

/**
 * The API for classes instrumented in "offline" mode. The agent configuration
 * is provided through system properties prefixed with <code>jacoco.</code>.
 */
public final class Offline {

	private static RuntimeData DATA;
	private static boolean reentryBarrier = false;

	public static boolean enabled = false;

	private Offline() {
		// no instances
	}

	/**
	 * API for offline instrumented classes.
	 * 
	 * @param classid
	 *            class identifier
	 * @param classname
	 *            VM class name
	 * @param probecount
	 *            probe count for this class
	 * @return probe array instance for this class
	 */
	public static boolean[] getProbes(final long classid,
			final String classname, final int probecount) {

		try {
			if (reentryBarrier == false && sun.misc.VM.isBooted()) {
				reentryBarrier = true;
				if (DATA == null) {

					final Properties config = new Properties();
					DATA = Agent.getInstance(new AgentOptions(config))
							.getData();
				}

				final boolean[] result = DATA.getExecutionData(
						Long.valueOf(classid), classname, probecount)
						.getProbes();

				return result;
			}
		} catch (final Throwable t) {
			// throw new RuntimeException(t);
		} finally {
			reentryBarrier = false;
		}
		return new boolean[probecount];
	}

}
