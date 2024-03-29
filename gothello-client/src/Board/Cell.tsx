import React from 'react';

export enum Stone {
  Black = "BLACK",
  White = "WHITE",
  None = "NONE"
}

export function charToStone(char: string): Stone {
  const lut : {[id:string]:Stone} =  {"L": Stone.None, "I": Stone.None, "W": Stone.White, "B": Stone.Black}
  return lut[char]
}

export enum StoneStyle {
  Captured = "captured",
  Flipped = "flipped",
  Normal = "normal",
  Placed = "placed"
}


interface CellProps {
  legal: Boolean;
  x: number;
  y: number;
  stone: Stone;
  stoneStyle: StoneStyle;
  onClick: Function;
  style: any;
}


export function Cell(props: CellProps): JSX.Element {
  // Wrapper to the provided onclick handler
  function onClick(e: any) {
    if (props.legal) {
      e.cell = {}
      e.cell.x = props.x
      e.cell.y = props.y
      props.onClick(e)
    }
  }

  return (
    <div
      onClick={onClick}
      style={props.style}
      className={"Cell " + (props.legal? "legal" : "illegal")}>
      <div className={`Stone ${props.stoneStyle} ${props.stone.toLowerCase()}`}></div>
    </div>)
}