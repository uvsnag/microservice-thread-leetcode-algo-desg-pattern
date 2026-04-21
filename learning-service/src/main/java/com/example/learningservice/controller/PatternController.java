package com.example.learningservice.controller;

import com.example.learningservice.pattern.adapter.AdapterDemo;
import com.example.learningservice.pattern.builder.BuilderDemo;
import com.example.learningservice.pattern.decorator.DecoratorDemo;
import com.example.learningservice.pattern.factory.FactoryDemo;
import com.example.learningservice.pattern.observer.ObserverDemo;
import com.example.learningservice.pattern.singleton.SingletonDemo;
import com.example.learningservice.pattern.strategy.StrategyDemo;
import com.example.learningservice.pattern.template.TemplateMethodDemo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/patterns")
public class PatternController {

    @GetMapping
    public Map<String, String> listPatterns() {
        Map<String, String> patterns = new LinkedHashMap<>();
        patterns.put("singleton", "/api/learning/patterns/singleton");
        patterns.put("factory", "/api/learning/patterns/factory");
        patterns.put("strategy", "/api/learning/patterns/strategy");
        patterns.put("observer", "/api/learning/patterns/observer");
        patterns.put("builder", "/api/learning/patterns/builder");
        patterns.put("decorator", "/api/learning/patterns/decorator");
        patterns.put("adapter", "/api/learning/patterns/adapter");
        patterns.put("template-method", "/api/learning/patterns/template-method");
        return patterns;
    }

    @GetMapping("/singleton")
    public Map<String, String> singleton() {
        return Map.of("pattern", "Singleton", "output", SingletonDemo.runDemo());
    }

    @GetMapping("/factory")
    public Map<String, String> factory() {
        return Map.of("pattern", "Factory", "output", FactoryDemo.runDemo());
    }

    @GetMapping("/strategy")
    public Map<String, String> strategy() {
        return Map.of("pattern", "Strategy", "output", StrategyDemo.runDemo());
    }

    @GetMapping("/observer")
    public Map<String, String> observer() {
        return Map.of("pattern", "Observer", "output", ObserverDemo.runDemo());
    }

    @GetMapping("/builder")
    public Map<String, String> builder() {
        return Map.of("pattern", "Builder", "output", BuilderDemo.runDemo());
    }

    @GetMapping("/decorator")
    public Map<String, String> decorator() {
        return Map.of("pattern", "Decorator", "output", DecoratorDemo.runDemo());
    }

    @GetMapping("/adapter")
    public Map<String, String> adapter() {
        return Map.of("pattern", "Adapter", "output", AdapterDemo.runDemo());
    }

    @GetMapping("/template-method")
    public Map<String, String> templateMethod() {
        return Map.of("pattern", "Template Method", "output", TemplateMethodDemo.runDemo());
    }
}
