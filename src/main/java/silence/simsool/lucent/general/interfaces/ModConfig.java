package silence.simsool.lucent.general.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import silence.simsool.lucent.general.enums.ConfigType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ModConfig {
	ConfigType type();
	String name();
	String display() default "";
	String description() default "";
	String category() default "General";
	double min() default 0.0;
	double max() default 10.0;
	double step() default 1.0;
	String[] options() default {};
	int priority() default 0;
	String parent() default "";

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Repeatable(CategoryPriority.List.class)
	public @interface CategoryPriority {
		String name();
		int priority();

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.TYPE)
		public @interface List {
			CategoryPriority[] value();
		}
	}
}