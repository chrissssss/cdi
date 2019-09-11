package info.balthaus.javaplayground.cdi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		System.out.println("Hello says " + App.class.getCanonicalName() + "!");
		Weld weld = new Weld().disableDiscovery().packages(App.class).property("org.jboss.weld.construction.relaxed",
				true);
		try (WeldContainer container = weld.initialize()) {
			container.select(InjectionPoint.class).get().letsNotCallItPrintButItPrints();
		}
	}
}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, TYPE })
@interface Validator {
}

interface Printable {
	void print();
}

class InjectionPoint {

	@Inject
	Printable printable;

	void letsNotCallItPrintButItPrints() {
		printable.print();
	}
}

@Default
class MetaPrinter implements Printable {

	@Validator
	@Inject
	private Instance<Printable> allPrintables;

	private List<Printable> cachedPrintables = null;

	@Override
	public void print() {
		getPrintablesSynchronized().forEach(Printable::print);
	}

	private synchronized List<Printable> getPrintablesSynchronized() {
		if (cachedPrintables == null) {
			return (cachedPrintables = allPrintables.stream().collect(Collectors.toList()));
		} else {
			return cachedPrintables;
		}
	}

}

@Validator
class Alternative1Impl implements Printable {

	@Override
	public void print() {
		System.out.println("Hello says " + this.getClass().getCanonicalName() + "!");
	}

}

@Validator
class Alternative2Impl implements Printable {

	@Override
	public void print() {
		System.out.println("Hello says " + this.getClass().getCanonicalName() + "!");

	}

}
