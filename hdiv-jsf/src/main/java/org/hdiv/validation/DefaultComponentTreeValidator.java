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
package org.hdiv.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.filter.ValidatorError;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validators.ComponentValidator;
import org.hdiv.validators.EditableValidator;
import org.hdiv.validators.GenericComponentValidator;
import org.hdiv.validators.HtmlInputHiddenValidator;
import org.hdiv.validators.UICommandValidator;

public class DefaultComponentTreeValidator implements ComponentTreeValidator {

	private static final Log log = LogFactory.getLog(DefaultComponentTreeValidator.class);

	protected final List<ComponentValidator> componentValidators = new ArrayList<ComponentValidator>();

	protected HDIVConfig config;

	public void createComponentValidators(final FacesContext facesContext) {

		componentValidators.add(new GenericComponentValidator());
		componentValidators.add(new HtmlInputHiddenValidator());
		componentValidators.add(new UICommandValidator());
		EditableValidator editableValidator = new EditableValidator();
		editableValidator.setHdivConfig(config);
		componentValidators.add(editableValidator);
	}

	public List<ValidatorError> validateComponentTree(final FacesContext facesContext, final UIComponent eventComponent) {

		PartialViewContext partialContext = facesContext.getPartialViewContext();
		if (partialContext.isPartialRequest()) {
			Collection<String> execIds = partialContext.getExecuteIds();

			for (String execId : execIds) {
				UIComponent execComp = facesContext.getViewRoot().findComponent(execId);
			}
		}

		ValidationContext context = new ValidationContext(facesContext, eventComponent);

		UIForm form = UtilsJsf.findParentForm(eventComponent);

		// Validate component tree starting in form
		validateComponentTree(context, form);

		List<ValidatorError> errors = checkParameters(context);

		errors.addAll(context.getErrors());
		return errors;
	}

	protected void validateComponentTree(final ValidationContext context, final UIComponent component) {

		validateComponent(context, component);

		if (context.hasErrors()) {
			// TODO stop or continue?
			return;
		}

		Iterator<UIComponent> it = component.getFacetsAndChildren();
		while (it.hasNext()) {
			UIComponent child = it.next();

			validateComponentTree(context, child);
		}

	}

	protected void validateComponent(final ValidationContext context, final UIComponent component) {

		for (ComponentValidator validator : componentValidators) {
			if (validator.supports(component)) {
				validator.validate(context, component);
			}
		}
	}

	protected List<ValidatorError> checkParameters(final ValidationContext context) {

		List<ValidatorError> errors = new ArrayList<ValidatorError>();

		for (String param : context.getRequestParameters().keySet()) {

			String value = context.getRequestParameters().get(param);
			Map<String, Set<Object>> validParameters = context.getValidParameters();

			boolean paramIsPressent = validParameters.keySet().contains(param);
			boolean paramValuePressent = false;
			if (paramIsPressent) {
				if (validParameters.get(param).contains(value)) {
					paramValuePressent = true;
				}
			}

			if (!paramIsPressent || !paramValuePressent) {

				if (!isExcludedParameter(context, param, value)) {

					if (log.isDebugEnabled()) {
						if (!paramIsPressent) {
							log.debug("Invalid parameter name: " + param);
						}
						else if (!paramValuePressent) {
							log.debug("Invalid parameter value for parameter: " + param + ". Valid values are: "
									+ validParameters.get(param));
						}
					}

					String type = paramIsPressent ? HDIVErrorCodes.PARAMETER_VALUE_INCORRECT : HDIVErrorCodes.PARAMETER_NOT_EXISTS;
					ValidatorError error = new ValidatorError(type, null, param, value);
					errors.add(error);
				}
			}
		}
		return errors;
	}

	protected boolean isExcludedParameter(final ValidationContext context, final String paramName, final String paramValue) {

		if (UtilsJsf.isFacesViewParamName(paramName)) {
			return true;
		}

		// TODO check startParameters and paramsWithoutValidation

		return false;
	}

	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

}