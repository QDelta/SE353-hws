GHC  ?= ghc
ODIR ?= build

all: main

main: $(ODIR)
	$(GHC) --make -O Main.hs -outputdir $(ODIR) -o main

$(ODIR):
	mkdir $(ODIR)

clean:
	rm -f main
	rm -r $(ODIR)

.PHONY: clean