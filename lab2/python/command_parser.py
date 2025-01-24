
import sys
from argparse import ArgumentParser, Namespace, SUPPRESS
from typing import Sequence, Optional, Any

try:
    # We try to import readline, because then the `input` function will use it.
    # But the readline library doesn't exist on Windows.
    import readline  # type: ignore
except ModuleNotFoundError:
    pass


class CommandParser(ArgumentParser):
    """
    This is an extension of `argparse.ArgumentParser`
    It has the additional feature that if the user doesn't specify 
    any arguments at all, the module will ask for them interactively.
    """

    def parse_args(self,  # type: ignore 
                   # ArgumentParser.parse_args has a slighlty more general type
                   args: Optional[Sequence[str]] = None,
                   namespace: Optional[Namespace] = None
            ) -> Namespace:
        args = sys.argv[1:] if args is None else list(args)
        if args:
            return super().parse_args(args, namespace)
        if namespace is None:
            namespace = Namespace()
        return self.parse_interactive(namespace)


    def parse_interactive(self, result: Namespace) -> Namespace:
        """
        Ask for the value of each argument interactively.
        """
        print("Note: You can also pass command-line arguments.")
        print()
        self.print_help()
        print()

        print("Enter values:")
        for action in self._actions: 
            if action.default == SUPPRESS:
                continue
            if action.nargs == "?" or action.nargs == "*":
                setattr(result, action.dest, None)
                continue

            prompt = " * " + (action.help or f"value for variable {action.dest}")
            if action.const:
                prompt += f"? (yes/+/{str(action.const).lower()} for {action.const})"
            if action.choices:
                prompt += f" (one of: {', '.join(action.choices)})"
            if action.default:
                prompt += f" (ENTER for {action.default})"
            elif action.const is True:
                prompt += f" (no/-/ENTER for False)"
            elif not action.required:
                prompt += f" (ENTER for nothing)"
            prompt += ": "

            value: Any = None
            while value is None:
                value = input(prompt)
                if not value:
                    value = action.default
                elif action.const:
                    value = value.lower()
                    if value in ("y", "yes", "+", str(action.const).lower()):
                        value = action.const
                    elif value in ("n", "no", "-"):
                        value = action.default
                    else:
                        print("ERROR: please answer yes or no")
                        value = None
                        continue
                elif action.type:
                    try:
                        value = action.type(value)
                    except ValueError as e:
                        print("ERROR:", e)
                        value = None
                        continue

                if value and action.choices:
                    if value not in action.choices:
                        print(f"ERROR: value must be one of: {', '.join(action.choices)}")
                        value = None
                        continue

                if not action.required:
                    break

            setattr(result, action.dest, value)

        return result

