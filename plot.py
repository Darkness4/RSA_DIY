import matplotlib.pyplot as plt

if __name__ == "__main__":
    xList = []
    yList = []
    prepend = "toBase2PowK"

    for i in range(1, 4):
        x = []
        y = []
        with open(f"{prepend}_{i}.txt", "r") as file:
            for line in file.readlines():
                xi, yi = map(int, line.split("\t"))
                x.append(xi)
                y.append(yi)
            xList.append(x)
            yList.append(y)

    fig, ax = plt.subplots()

    for i, (x, y) in enumerate(zip(xList, yList)):
        ax.plot(x, y, label=f"Try n°{i}")

    ax.set(
        xlabel="Base (2^)",
        ylabel="Duration (ns)",
        title=f"Duration of {prepend} depending on the base",
    )
    ax.grid()
    ax.legend()

    fig.savefig(f"{prepend}.png")
    plt.show()

