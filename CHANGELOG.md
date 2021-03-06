# Changelog

## 0.0.12 - 20191107

**Breaking** changes in the name of performance:

- moved the `parse` namespace functions for the `core/html` macro into `core`, removing the `parse` namespace
- make `ele` construct its preact js object and props js value for that object at compile-time with `cljs.tagged-literals/read-js` instead of using bean. This is so much faster it's ridiculous.
- similarly, the `children` value on `props` is now converted to a js array with `read-js` for normal children, instead of being converted implicitly by `cljs-bean`. This broke handling of the output of collections produced by `for`, so we're covering that case in the macro by wrapping the for call in a `clj->js` to convert the resulting list into a js array. This is getting pretty hacky, and since templates are specified at compile-time and not generated via data, I'm beginning to think that providing a macro to transform hiccup isn't worth it, and instead going the route of providing macros for each element, such as `(dom/div ...)` instead, which would decomplect the whole thing nicely.
- `precog.styled/css` no longer converts clojure maps to javascript, you have to provide javascript objects directly. I'm thinking about how to rework this as a macro that can do the conversion at compile-time, but doing this at runtime required a lot of overhead.

Bugs:

- fix a bug with the previous change to `use-focus`

## 0.0.11 - 20191105

- for `use-focus`, only call hook's update function when the value of the focus is not equal to the previous value

## 0.0.10 - 20191105

- *breaking* make `use-memo` a macro instead of a redef of `hooks/useMemo`. It converts the dependencies argument to a javascript array for you.
- *breaking* make `use-callback` a macro instead of a redef of `hooks/useCallback`. It converts the dependencies argument to a javascript array for you
- add `bind-memo`, similar to `use-memo`, but partially applies the deps to the memo'd function. This is the replacement for re-frame's subscription chaining. See the demo for an example of how this works.
- add `bind-callback`, similar to `use-callback`, but partially applies the deps to the callback function. See the demo for examples on how this makes for really easy event handlers. This is what `bind-handler` wanted to be.

## 0.0.9 - 20191025

Lots of breaking changes here:

- *breaking* move `precog.main` to `precog.core`, split `html` macro into a clj namespace and main cljs functions into theirs; parse/ele go into `precog.parse` cljc namespace.
- fix demo breakage from previous breaking out into functions
- extract atom-watching helper into own function
- *breaking* rename `use-lens` to `use-focus`, since _lens_ implies two-way binding
- *maybe breaking* remove unused cljss library from deps
- add experimental `styled` component helper relying on emotion

## 0.0.8 - 20191018

- *breaking* remove `bind-handler`, it doesn't provide much real wins over inline functions, at much higher complexity.
- `use-lens` now accepts supplimental arguments which will be applied to the lens function alongside the state.

## 0.0.7 - 20191015

- Fix a bug with the previous `bind-handler` change. I should probably start thinking about tests.

## 0.0.6 - 20191015

- moved a small bit of the compiled output back to a more verbose form, it saved virtually nothing in a gzipped bundle and cost a few milliseconds of render time
- *breaking* `bind-handler` now requires an event-flags map as its second argument, and accepts supplimental arguments that will be passed to the handling function. So `(bind-handler #{} myfn 1 2)` will call `(myfn state event 1 2)`

## 0.0.5 - 20191013

- A few small tweaks to the element constructor and preliminary tests show reducing the re-gzip bundle size in `:advanced` compilation mode of the precompiled templates from 50kb to 8kb.

## 0.0.4 - 20191010

- Move HTML file for demo out of `target/` and to `example/` so lein doesn't clobber it on builds
- Basic support for parsing the contents of some expressions:
    - `list`: not sure why you'd need this, but it becomes a fragment
    - `do`, `for`, `let`, `if` (and its ilk), `when` (and its ilk), `case`, `cond` have their expressions parsed
  I havent' ever needed to use threading macros in the process of constructing a view, and feel supporting those
  is not worth the effort at this point.
- New & Imporved `use-atom` that doesn't make a new atom on every change
- Added `bind-handler` whichs takes an atom, an optional set of event-processing flags, and a processing function,
  and swaps the contents of the atom with the processing function, perhaps stopping or extracting the event's target first.

## 0.0.3 - 20191010

- Don't rename props such as `on-input` to `onInput` anymore. Perhaps it could be done for DOM elements and not JS components, but the inconsistency would bug me. We'd have to offer a way to supplant the built-in transforms, and then they couldn't be done at compile-time. I feel this is one of those things better shoved into the face of the library consumer.

## 0.0.2 - 20191008

- Make clojure maps the default props for function components. Use bean to convert props to js for DOM components or other components that have been "annotated" with `precog/use-js-props`

## 0.0.1 - 20191001

- Initial Release
