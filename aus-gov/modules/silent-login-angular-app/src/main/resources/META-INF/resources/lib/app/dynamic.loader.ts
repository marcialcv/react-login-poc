import {
	ApplicationRef,
	ComponentFactoryResolver,
	Injector,
	NgZone,
	Type,
} from '@angular/core';


export class DynamicLoader {
	constructor(private injector: Injector) {}

	// Load an Angular component dinamically so that we can attach it to
	// the portlet's DOM, which is different for each portlet instance and,
	// thus, cannot be determined until the page is rendered (during runtime).

	loadComponent<T>(component: Type<T>, dom: Element, config: any) {
		(<NgZone>this.injector.get(NgZone)).run(() => {
			const componentFactory = this.injector
				.get(ComponentFactoryResolver)
				.resolveComponentFactory(component);
			const componentRef = componentFactory.create(
				this.injector,
				[],
				dom
			);
			componentRef.instance["config"] = config;
			this.injector.get(ApplicationRef).attachView(componentRef.hostView);
		});
	}
}