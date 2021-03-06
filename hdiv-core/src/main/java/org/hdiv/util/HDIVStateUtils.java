/**
 * Copyright 2005-2016 hdiv.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.util;

import org.hdiv.exception.HDIVException;

public final class HDIVStateUtils {

	private HDIVStateUtils() {
	}

	public static int numParts(final String hdivState) {
		return hdivState.split(Constants.STATE_ID_STR_SEPARATOR).length;
	}

	public static String encode(final int pageId, final int stateId, final String suffix) {
		// 11-0-C1EF82C48A86DE9BB907F37454998CC3
		return new StringBuilder(40).append(pageId).append(Constants.STATE_ID_SEPARATOR).append(stateId)
				.append(Constants.STATE_ID_SEPARATOR).append(suffix).toString();
	}

	public static int getPageId(final String stateId) {
		int firstSeparator = stateId.indexOf(Constants.STATE_ID_SEPARATOR);
		if (firstSeparator == -1) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
		try {
			return Integer.parseInt(stateId.substring(0, firstSeparator));
		}
		catch (NumberFormatException ex) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
	}

	public static int getStateId(final String stateId) {
		int start = stateId.indexOf(Constants.STATE_ID_SEPARATOR);
		try {
			return Integer.parseInt(stateId.substring(start + 1, stateId.indexOf(Constants.STATE_ID_SEPARATOR, start + 1)));
		}
		catch (NumberFormatException ex) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
	}

	public static String getScopedState(final int stateId, final String token) {
		return Integer.toString(stateId) + Constants.STATE_ID_SEPARATOR + token;
	}

	public static int getStateFromScoped(final String scoped) {
		try {
			return Integer.parseInt(scoped.substring(0, scoped.indexOf(Constants.STATE_ID_SEPARATOR)));
		}
		catch (NumberFormatException ex) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
	}

}
