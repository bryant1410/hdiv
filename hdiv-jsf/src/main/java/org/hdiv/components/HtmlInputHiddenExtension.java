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
package org.hdiv.components;

import java.io.IOException;
import java.util.Collection;

import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.state.StateManager;
import org.hdiv.util.ConstantsJsf;

/**
 * HtmlInputHidden component extension
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlInputHiddenExtension extends HtmlInputHidden {

	private static final Log log = LogFactory.getLog(HtmlInputHiddenExtension.class);

	/**
	 * Obtains hidden real value which has been stored in the JSF state
	 * 
	 * @param clientId ClientId value
	 * 
	 * @return hidden real value
	 */
	public Object getStateValue(final FacesContext context, final String clientId) {

		Object val = getValue();

		StateManager stateManager = getStateManager(context);
		if (stateManager != null) {
			Collection<Object> values = stateManager.restoreState(clientId);
			if (values != null && values.size() > 0) {
				val = values.iterator().next();
			}
		}

		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context. FacesContext)
	 */
	@Override
	public void encodeBegin(final FacesContext context) throws IOException {

		String clientId = this.getClientId(context);
		Object val = super.getValue();

		StateManager stateManager = getStateManager(context);
		if (stateManager != null) {
			stateManager.saveState(clientId, val);
		}

		if (log.isDebugEnabled()) {
			log.debug("Hidden real value :" + val);
		}

		super.encodeBegin(context);
	}

	protected StateManager getStateManager(final FacesContext context) {
		StateManager stateManager = (StateManager) context.getExternalContext().getRequestMap()
				.get(ConstantsJsf.HDIV_STATE_MANAGER_ATTRIBUTE_KEY);
		return stateManager;
	}

}
