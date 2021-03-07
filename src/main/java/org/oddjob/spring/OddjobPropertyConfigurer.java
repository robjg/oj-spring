package org.oddjob.spring;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.arooa.runtime.RetainUnexpandedStrings;
import org.oddjob.arooa.runtime.SubstituationPolicySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringValueResolver;

/**
 * An {@link BeanFactoryPostProcessor} that will replace ${} property 
 * place holders with values resolved from Oddjob. If Oddjob can't resolve
 * the place holder it is left as is.
 * <p>
 * Oddjob's property expression parser is based on Ant's and has the same
 * behaviour with regard to replacing $$ with a single $ but in this
 * implementation the expression only gets evaluated if it is not constant
 * i.e. it contains a ${}.
 * <p>
 * This is confusing so here's some examples. If oddjob contains the 
 * property definition <code>favourite.fruit=apples</code> and 
 * <code>favourite.snack=favourite.fruit</code> then:
 * <ul>
 * <li>'My $${favourite.fruit} is ${favourite.fruit}' becomes 
 * 'My ${favourite.fruit} is apples'</li>
 * <li>'My $${$${favourite.snack}} is also $${${favourite.snack}} which is ${${favourite.snack}}'
 * becomes My '${${favourite.snack}} is also ${favourite.fruit} which is apples'</li>
 * <li>'There's no ${favourite.pizza}' stays the same. The favourite.pizza resolves to null
 * so the property expansion expression is preserved.</li>
 * <li>'$${favourite.pizza} still has two $$s' stays the same as this expression is constant.</li>
 * <li>'But $${favourite.pizza} with ${favourite.pizza} looses one $' becomes 
 * 'But ${favourite.pizza} with ${favourite.pizza} looses one $' because it
 * isn't constant despite the fact that favourite.pizza could not be 
 * resolved.</li>
 * </ul>
 * 
 * 
 * @author rob
 *
 */
public class OddjobPropertyConfigurer implements BeanFactoryPostProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(
			OddjobPropertyConfigurer.class);
	
	/** The Oddjob session used to resolve property values. */
	private final ArooaSession session;
	
	/**
	 * Constructor.
	 * 
	 * @param session The session from Oddjob.
	 */
	public OddjobPropertyConfigurer(ArooaSession session) {
		this.session = new SubstituationPolicySession(
				session, new RetainUnexpandedStrings());
	}
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactoryToProcess)
			throws BeansException {
		
		StringValueResolver valueResolver = new BeanDirectoryResolver();

		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(
				valueResolver);
		
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (String beanName : beanNames) {

			BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanName);
			try {
				visitor.visitBeanDefinition(bd);
			} catch (BeanDefinitionStoreException ex) {
				throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName, ex.getMessage());
			}
		}
		
		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		beanFactoryToProcess.resolveAliases(valueResolver);

		// New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
		beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
	}
	
	/**
	 * Resolves ${} expressions as if in Oddjob. 
	 *
	 */
	class BeanDirectoryResolver implements StringValueResolver {

		private final ExpressionParser parser; 
		
		public BeanDirectoryResolver() {
			parser = session.getTools().getExpressionParser();
		}
		
		public String resolveStringValue(String strVal) {
			ParsedExpression expression = parser.parse(strVal);
			// If an expression is constant we don't even try to
			// evaluate it.
			if (expression.isConstant()) {
				return strVal;
			}
			try {
				String result =
						expression.evaluate(session, String.class);
				if (!strVal.equals(result)) {
					logger.debug("Replaced [" + strVal + 
							"] with [" + result + "]");
				}
				return result;
			}
			catch (ArooaConversionException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
