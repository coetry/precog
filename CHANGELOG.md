# Changelog

## 0.0.3 - 20191010

- Don't rename props such as `on-input` to `onInput` anymore. Perhaps it could be done for DOM elements and not JS components, but the inconsistency would bug me. We'd have to offer a way to supplant the built-in transforms, and then they couldn't be done at compile-time. I feel this is one of those things better shoved into the face of the library consumer.

## 0.0.2 - 20191008

- Make clojure maps the default props for function components. Use bean to convert props to js for DOM components or other components that have been "annotated" with `precog/use-js-props`

## 0.0.1 - 20191001

- Initial Release