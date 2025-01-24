
from sys import stderr
from time import time
from typing import Iterator, Iterable, TypeVar
from types import TracebackType


T = TypeVar("T")

class ProgressBar(Iterable[T]):
    """
    A simple progress bar, inspired by the `tqdm` module.
    For "real" programs, consider installing `tqdm` instead,
    it has many more features (such as nested progress bars).
    """
    # Class-variable to turn on/off all progress bars.
    visible: bool = True

    # Instance variables.
    iterator: Iterator[T]
    description: str
    startTime: float
    total: int
    n: int
    interval: int
    unit: int
    unitSuffix: str
    barWidth: int
    descrWidth: int

    def __init__(self, iterable: Iterable[T] = (), description: str = "Logging", 
                 total: int = 0, unit: int = 0, 
                 barWidth: int = 40, descrWidth: int = 25):
        self.startTime = time()
        self.iterator = iter(iterable)
        self.description = description
        self.total = total
        if not self.total:
            self.total = len(iterable)  # type: ignore
        self.n = 0
        self.interval = max(1, min(self.total//200, 100))
        if unit == 0:
            unit = 1 if self.total < 20_000 else 1_000 if self.total < 20_000_000 else 1_000_000
        self.unit = unit
        if unit == 1:
            self.unitSuffix = " "
        elif unit == 1_000:
            self.unitSuffix = "k"
        elif unit == 1_000_000:
            self.unitSuffix = "M"
        else:
            raise ValueError("Can only handle unit == 1 or 1000 or 1_000_000")
        self.barWidth = barWidth
        self.descrWidth = descrWidth
        self._printInfoline()

    def __iter__(self) -> Iterator[T]:
        return self

    def __next__(self) -> T:
        try:
            el = next(self.iterator)
        except StopIteration:
            self._printInfoline()
            self._closeInfoline()
            raise
        self.n += 1
        if self.n % self.interval == 0:
            self._printInfoline()
        return el

    def __enter__(self) -> 'ProgressBar[T]':
        return self

    def __exit__(self, exc_type: BaseException, exc_val: BaseException, exc_tb: TracebackType):
        self._printInfoline()
        self._closeInfoline()
        pass

    def setValue(self, n: int):
        """Sets a new value for the counter. It has to be larger than the old value."""
        if n > self.n:
            self.n = n
            if n % self.interval == 0:
                self._printInfoline()

    def update(self, add: int):
        """Increases the counter by a give number."""
        self.n += add
        if self.n % self.interval == 0:
            self._printInfoline()

    def _printInfoline(self):
        if ProgressBar.visible:
            percent = 0.0
            if self.total > 0:
                percent = self.n / self.total
            hashes = round(percent * self.barWidth)
            pbar = "[" + "=" * hashes + "·" * (self.barWidth-hashes) + "]"
            elapsed = time() - self.startTime
            print(
                f"{self.description:{self.descrWidth}s} {percent:4.0%} {pbar} "
                f"{self.n/self.unit:6.0f}{self.unitSuffix} "
                f"of {self.total/self.unit:.0f}{self.unitSuffix}  | {elapsed:6.1f} s",
                file=stderr, end="\r", flush=True
            )

    def _closeInfoline(self):
        if ProgressBar.visible:
            print(file=stderr)

