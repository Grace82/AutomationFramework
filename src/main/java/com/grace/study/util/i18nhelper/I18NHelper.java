package com.grace.study.util.i18nhelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



import com.grace.study.util.HsqldbUtil;
import com.grace.study.util.loghelper.LoggedTestCase;

public class I18NHelper extends LoggedTestCase{
	private static Map<String,String> cachedI18NKeys = Collections.synchronizedMap(new HashMap<String,String>());
	private static String getValue(String key){
		String value = null;
		if(cachedI18NKeys.containsKey(key)){
			value = cachedI18NKeys.get(key);
		}else{
			try{
				value = getValueFromServer(key);
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			logger.debug("::::: key is [" + key + "] and value is [" + "] ");
			cachedI18NKeys.put(key, value);
		}
		return value;
	}
	
	private static String getValueFromServer(String key){
		String value = null;
		try{
			value = HsqldbUtil.getString(key);
		}catch(UnsupportedOperationException e){
			logger.info(e.getMessage());
		}
		return value;
	}
	
	public static void processI18NKeys(Object object){
		logger.info("###############################################################################");
		boolean objectIsType = object instanceof Class;
		Class<?> type = objectIsType ? (Class<?>) object : object.getClass();
		do{
			for (Field field : type.getDeclaredFields()){
				I18N i18nKey = field.getAnnotation(I18N.class);
				boolean fieldIsAnnotated = i18nKey != null;
				boolean fieldIsString = field.getType() == String.class;
				boolean fieldIsNonFinal = !Modifier.isFinal(field.getModifiers());
				
				if(fieldIsAnnotated && fieldIsString && fieldIsNonFinal){
					String key = i18nKey.value();
					String value = getValue(key);
					try{
						field.setAccessible(true);
						field.set(object, value);
					}catch(IllegalAccessException e){
						logger.info("Failed to process @I18N annotation!" + e.getMessage());
					}
				}
			}
			type = type.getSuperclass();
		}while(type != null);
		
	}
}
